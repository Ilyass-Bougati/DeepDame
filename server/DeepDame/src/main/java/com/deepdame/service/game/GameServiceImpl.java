package com.deepdame.service.game;

import com.deepdame.dto.game.GameDto;
import com.deepdame.dto.game.GameMapper;
import com.deepdame.engine.core.logic.GameEngine;
import com.deepdame.engine.core.model.GameState;
import com.deepdame.engine.core.model.Move;
import com.deepdame.engine.core.model.PieceType;
import com.deepdame.entity.GameDocument;
import com.deepdame.enums.GameMode;
import com.deepdame.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;
    private final GameEntityServiceImpl gameEntityService;
    private final GameMapper gameMapper;

    private final GameEngine gameEngine = new GameEngine();


    @Override
    public GameDto findById(UUID id){
        GameDocument entity = gameEntityService.findById(id);
        return gameMapper.toDTO(entity);
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
        gameRepository.deleteById(id);
    }


    @Override
    public GameDto createGame(UUID playerID, GameMode gameMode){
        UUID gameId = UUID.randomUUID();

        GameState gameState = new GameState(gameId);

        GameDocument gameDoc = GameDocument.builder()
                .id(gameId)
                .gameState(gameState)
                .mode(gameMode)
                .playerBlackId(playerID)
                .build();

        return gameMapper.toDTO(gameRepository.save(gameDoc));
    }

    @Override
    public GameDto joinGame(UUID gameId, UUID playerId){

        GameDocument gameDoc = gameEntityService.findById(gameId);

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
        return gameMapper.toDTO(gameRepository.save(gameDoc));
    }

    @Override
    public GameDto makeMove(UUID gameId, UUID playerId, Move move){

        GameDocument gameDoc = gameEntityService.findById(gameId);
        GameState currentState = gameDoc.getGameState();

        if (currentState.isGameOver()){
            throw new IllegalStateException("Game is Over");
        }

        validatePlayerTurn(gameDoc, playerId, currentState.getCurrentTurn());

        GameState newState = gameEngine.applyMove(currentState, move);

        gameDoc.setGameState(newState);
        GameDocument savedGame = gameRepository.save(gameDoc);

        // Calling the ai move

        return gameMapper.toDTO(savedGame);
    }

    @Override
    public GameDto surrenderGame(UUID gameId, UUID playerId){

        GameDocument gameDoc = gameEntityService.findById(gameId);
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
        return gameMapper.toDTO(gameRepository.save(gameDoc));
    }

    @Override
    public List<GameDto> getOpenGames(){
        return gameEntityService.findOpenPvpGames().stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    public List<GameDto> getUserGames(UUID playerId){
        return gameEntityService.findGamesByPlayerId(playerId).stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    private void validatePlayerTurn(GameDocument gameDoc, UUID playerId, PieceType currentTurn){

        boolean isBlack = playerId.equals(gameDoc.getPlayerBlackId());
        boolean isWhite = playerId.equals(gameDoc.getPlayerWhiteId());

        if (!isBlack && !isWhite){
            throw new IllegalStateException("WHO TF ARE YOU !!!!");
        }
        if (isBlack && currentTurn != PieceType.BLACK){
            throw new IllegalStateException("I'm not racist but it is White's turn");
        }
        if (isWhite && currentTurn != PieceType.WHITE){
            throw new IllegalStateException("It is Black's turn you little racist");
        }
    }

}
