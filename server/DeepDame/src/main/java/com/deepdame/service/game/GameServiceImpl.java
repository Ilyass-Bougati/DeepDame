package com.deepdame.service.game;

import com.deepdame.dto.game.GameDto;
import com.deepdame.dto.game.GameMapper;
import com.deepdame.engine.core.logic.GameEngine;
import com.deepdame.engine.core.model.GameState;
import com.deepdame.engine.core.model.Move;
import com.deepdame.engine.core.model.PieceType;
import com.deepdame.entity.GameDocument;
import com.deepdame.enums.GameMode;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.GameRepository;
import com.deepdame.service.cache.GameCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameCacheService gameCacheService;
    private final GameRepository gameRepository;
    private final GameEntityService gameEntityService;
    private final GameMapper gameMapper;

    private final GameEngine gameEngine = new GameEngine();

    @Override
    public GameDto findById(UUID id){
        GameDocument gameDoc = gameCacheService.getGame(id);

        if (gameDoc == null) {
            gameDoc = gameEntityService.findById(id);
        }

        return gameMapper.toDTO(gameDoc);
    }

    @Override
    public GameDto save(GameDto gameDto){
        throw new UnsupportedOperationException("Use createGame(playerId, mode) to start a new game.");
    }

    @Override
    public GameDto update(GameDto gameDto){
        throw new UnsupportedOperationException(
                "Games cannot be generically updated. Use makeMove(), joinGame(), or surrenderGame() instead."
        );
    }

    @Override
    public void delete(UUID id) {
        gameCacheService.deleteGame(id);
    }


    @Override
    public GameDto createGame(UUID playerID, GameMode gameMode){

        if (gameCacheService.isUserPlaying(playerID)){
            throw new IllegalStateException("you are alredy in an active game.");
        }

        UUID gameId = UUID.randomUUID();

        GameDocument gameDoc = GameDocument.builder()
                .id(gameId)
                .mode(gameMode)
                .playerBlackId(playerID)
                .gameState(new GameState(gameId))
                .history(new ArrayList<>())
                .build();

        gameCacheService.saveGame(gameDoc);
        gameCacheService.setUserCurrentGame(playerID, gameId);

        if (gameMode == GameMode.PVP){
            gameCacheService.addToLobby(gameId);
        }

        return gameMapper.toDTO(gameDoc);
    }

    @Override
    public GameDto joinGame(UUID gameId, UUID playerId){

        if (gameCacheService.isUserPlaying(playerId)){
            throw new IllegalStateException("You are already in an active game.");
        }

        GameDocument gameDoc = gameCacheService.getGame(gameId);

        if (gameDoc.getMode() != GameMode.PVP){
            throw new IllegalStateException("Cannot join a PVE game");
        }
        if (gameDoc.getPlayerWhiteId() != null){
            throw new IllegalStateException("Game is full");
        }
        if (gameDoc.getPlayerBlackId().equals(playerId)){
            throw new IllegalStateException("You cannot play against yourself");
        }

        gameDoc.setPlayerWhiteId(playerId);

        gameCacheService.saveGame(gameDoc);
        gameCacheService.setUserCurrentGame(playerId, gameId);
        gameCacheService.removeFromLobby(gameId);

        return gameMapper.toDTO(gameDoc);
    }

    @Override
    public GameDto findOrStartMatch(UUID playerId){

        if (gameCacheService.isUserPlaying(playerId)) {
            UUID activeGameId = gameCacheService.getUserCurrentGameId(playerId);
            return findById(activeGameId);
        }

        for (int i = 0; i < 3; i++) {
            UUID openGameId = gameCacheService.getRandomOpenGameId();

            if (openGameId != null) {
                try {
                    return joinGame(openGameId, playerId);
                } catch (Exception e) {
                    throw new NotFoundException("Unable to find a game");
                }
            }
        }

        return createGame(playerId, GameMode.PVP);
    }

    @Override
    public GameDto makeMove(UUID gameId, UUID playerId, Move move){

        GameDocument gameDoc = gameCacheService.getGame(gameId);
        if (gameDoc == null) throw new NotFoundException("Game not found or expired");
        GameState currentState = gameDoc.getGameState();

        if (currentState.isGameOver()){
            throw new IllegalStateException("Game is Over");
        }

        validatePlayerTurn(gameDoc, playerId, currentState.getCurrentTurn());

        GameState newState = gameEngine.applyMove(currentState, move);

        gameDoc.setGameState(newState);
        gameDoc.getHistory().add(move);

        if (newState.isGameOver()){
            handleGameOver(gameDoc);
        } else {
            gameCacheService.saveGame(gameDoc);
        }

        // Calling the ai move

        return gameMapper.toDTO(gameDoc);
    }

    @Override
    public GameDto surrenderGame(UUID gameId, UUID playerId){

        GameDocument gameDoc = gameCacheService.getGame(gameId);
        if (gameDoc == null) throw new NotFoundException("Game not found");
        GameState gameState = gameDoc.getGameState();

        if (gameState.isGameOver()){
            throw new IllegalStateException("Game already over");
        }

        if (playerId.equals(gameDoc.getPlayerBlackId())){
            gameState.finishGame(PieceType.WHITE);
        } else if (playerId.equals(gameDoc.getPlayerWhiteId())){
            gameState.finishGame(PieceType.BLACK);
        } else {
            throw new IllegalStateException("Not player in this game");
        }

        gameDoc.setGameState(gameState);
        handleGameOver(gameDoc);

        return gameMapper.toDTO(gameDoc);
    }

    @Override
    public List<GameDto> getOpenGames(){
        return gameCacheService.getOpenGames().stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    public List<GameDto> getUserFinishedGames(UUID playerId){
        return gameEntityService.findGamesByPlayerId(playerId).stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    public GameDto getUserCurrentGame(UUID playerId){
        UUID gameId = gameCacheService.getUserCurrentGameId(playerId);
        if (gameId == null) return null; // im not sure if i should return null ot throw an exception here

        GameDocument gameDoc = gameCacheService.getGame(gameId);
        return gameMapper.toDTO(gameDoc);
    }

    private void validatePlayerTurn(GameDocument gameDoc, UUID playerId, PieceType currentTurn){

        boolean isBlack = playerId.equals(gameDoc.getPlayerBlackId());
        boolean isWhite = playerId.equals(gameDoc.getPlayerWhiteId());

        if (!isBlack && !isWhite){
            throw new IllegalArgumentException("Player is not a participant in this game.");
        }
        if (isBlack && currentTurn != PieceType.BLACK){
            throw new IllegalStateException("It is currently White's turn.");
        }
        if (isWhite && currentTurn != PieceType.WHITE){
            throw new IllegalStateException("It is currently Black's turn.");
        }
    }

    private void handleGameOver(GameDocument doc) {

        PieceType winner = doc.getGameState().getWinner();

        if (winner.equals(PieceType.BLACK)) {
            doc.setWinnerId(doc.getPlayerBlackId());
        } else {
            doc.setWinnerId(doc.getPlayerWhiteId());
        }

        gameRepository.save(doc);

        gameCacheService.deleteGame(doc.getId());
        gameCacheService.removeFromLobby(doc.getId());

        gameCacheService.clearUserCurrentGame(doc.getPlayerBlackId());
        if (doc.getPlayerWhiteId() != null) {
            gameCacheService.clearUserCurrentGame(doc.getPlayerWhiteId());
        }
    }
}
