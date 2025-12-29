package com.deepdame;

import com.deepdame.dto.game.GameDto;
import com.deepdame.engine.core.model.*;
import com.deepdame.enums.GameMode;
import com.deepdame.repository.GameRepository;
import com.deepdame.service.game.GameEntityService;
import com.deepdame.service.game.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    @Test
    @DisplayName("create a PVE Game")
    void testCreateGamePVE() {

        UUID playerId = UUID.randomUUID();
        GameDto game = gameService.createGame(playerId, GameMode.PVE);

        assertNotNull(game.getId());
        assertEquals(GameMode.PVE, game.getMode());
        assertEquals(playerId, game.getPlayerBlackId());
        assertNull(game.getPlayerWhiteId(), "PVE White player ID should be null in DTO");
        assertNotNull(game.getGameState(), "Game state must be initialized");
    }

    @Test
    @DisplayName("create a PVP Game")
    void testCreatPVP(){

        UUID playerId = UUID.randomUUID();
        GameDto game = gameService.createGame(playerId, GameMode.PVP);

        assertEquals(GameMode.PVP, game.getMode());
        assertNull(game.getPlayerWhiteId(), "White player should be null until someone joins");
    }


    @Test
    @DisplayName("second player join a PVP game")
    void testJoinGameSuccess() {

        UUID player1 = UUID.randomUUID();
        GameDto game = gameService.createGame(player1, GameMode.PVP);

        UUID player2 = UUID.randomUUID();
        GameDto joined = gameService.joinGame(game.getId(), player2);

        assertEquals(player2, joined.getPlayerWhiteId());
        assertEquals(player1, joined.getPlayerBlackId());
    }

    @Test
    @DisplayName("fial if player tries to join their own game")
    void testSelfJoinGame() {

        UUID player1 = UUID.randomUUID();
        GameDto game = gameService.createGame(player1, GameMode.PVP);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            gameService.joinGame(game.getId(), player1);
        });

        assertEquals("You cannot play against yourself", exception.getMessage());
    }

    @Test
    @DisplayName("fial if game is already full")
    void testJoinGameFailFull() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();

        GameDto game = gameService.createGame(p1, GameMode.PVP);
        gameService.joinGame(game.getId(), p2);

        assertThrows(IllegalStateException.class, () -> {
            gameService.joinGame(game.getId(), p3);
        });
    }

    @Test
    @DisplayName("make a valid move successfully")
    void testMakeMoveSuccess() {
        UUID p1 = UUID.randomUUID();
        GameDto game = gameService.createGame(p1, GameMode.PVE);

        GameDto updatedGame = gameService.makeMove(game.getId(), p1,  new Move(new Position(5 , 0), new Position(4, 1)));

        assertEquals(PieceType.WHITE, updatedGame.getGameState().getCurrentTurn());

        Piece piece = updatedGame.getGameState().getBoard().getPiece(new Position(4, 1));

        assertEquals(Piece.regular(PieceType.BLACK), piece);
        assertNull(updatedGame.getGameState().getBoard().getPiece(new Position(5, 0)));

    }

    @Test
    @DisplayName("make an invalid move successfully")
    void testMakeMoveInvalid() {
        UUID p1 = UUID.randomUUID();
        GameDto game = gameService.createGame(p1, GameMode.PVE);

        Exception exception1 = assertThrows(IllegalArgumentException.class, ()->{
            gameService.makeMove(game.getId(), p1,  new Move(new Position(5 , 0), new Position(4, 0)));
        });

        Exception exception2 = assertThrows(IllegalArgumentException.class, ()->{
            gameService.makeMove(game.getId(), p1,  new Move(new Position(5 , 1), new Position(4, 0)));
        });

        assertEquals("Invalide move",exception1.getMessage());
        assertEquals("Invalide move",exception2.getMessage());
    }

    @Test
    @DisplayName("Moving out of turn")
    void testFailWrongTurn(){
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        GameDto game = gameService.createGame(p1, GameMode.PVP);
        gameService.joinGame(game.getId(), p2);

        Exception exception1 = assertThrows(IllegalStateException.class, ()->{
            gameService.makeMove(game.getId(), p2, new Move(new Position(5, 0), new Position(4, 1)));
        });

        Exception exception2 = assertThrows(IllegalStateException.class, ()->{
            gameService.makeMove(game.getId(), p2, new Move(new Position(2, 1), new Position(3, 0)));
        });

        assertEquals("It is Black's turn you little racist",exception1.getMessage());
        assertEquals("It is Black's turn you little racist",exception2.getMessage());
    }

    @Test
    @DisplayName("random person tries to move")
    void testMakeMoveFailSpectator() {
        UUID p1 = UUID.randomUUID();
        UUID randomGuy = UUID.randomUUID();

        GameDto game = gameService.createGame(p1, GameMode.PVE);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameService.makeMove(game.getId(), randomGuy, new Move(new Position(5, 0), new Position(4, 1)));
        });

        assertEquals("WHO TF ARE YOU !!!!",exception.getMessage());

    }

    @Test
    @DisplayName("Test surrender")
    void testSurrender(){
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        GameDto game = gameService.createGame(p1, GameMode.PVP);
        gameService.joinGame(game.getId(), p2);

        GameDto end = gameService.surrenderGame(game.getId(), p1);

        assertTrue(end.getGameState().isGameOver());
        assertEquals(PieceType.WHITE, end.getGameState().getWinner());

    }

    @Test
    @DisplayName("sould list all open PVP games only")
    void testGetOpenGmaes(){
        UUID p1 = UUID.randomUUID();

        gameService.createGame(p1, GameMode.PVP);
        gameService.createGame(p1, GameMode.PVE);

        GameDto fullGmae =  gameService.createGame(p1, GameMode.PVP);
        gameService.joinGame(fullGmae.getId(), UUID.randomUUID());

        List<GameDto> openGames = gameService.getOpenGames();

        assertEquals(1, openGames.size());
        assertEquals(GameMode.PVP, openGames.get(0).getMode());

    }

    @Test
    @DisplayName("Block generic update calls")
    void testBlockGenericUpdate() {
        UUID p1 = UUID.randomUUID();
        GameDto game = gameService.createGame(p1, GameMode.PVE);

        assertThrows(UnsupportedOperationException.class, () -> {
            gameService.update(game);
        });
    }

    @Test
    @DisplayName("Block generic update calls")
    void testBlockGenericSave() {
        GameDto dummyDto = GameDto.builder()
                .id(UUID.randomUUID())
                .mode(GameMode.PVP)
                .build();

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            gameService.save(dummyDto);
        });

        assertEquals("Use createGame(playerId, mode) to start a new game.", exception.getMessage());
    }
}
