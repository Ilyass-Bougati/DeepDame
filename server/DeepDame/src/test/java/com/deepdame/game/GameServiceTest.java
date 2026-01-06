package com.deepdame.game;

import com.deepdame.dto.game.GameDto;
import com.deepdame.engine.core.model.*;
import com.deepdame.enums.GameMode;
import com.deepdame.repository.mongo.GameRepository;
import com.deepdame.service.game.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private StringRedisTemplate stringTemplate;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        Objects.requireNonNull(stringTemplate.getConnectionFactory()).getConnection().serverCommands().flushDb();
    }

    @Test
    @DisplayName("create a PVE Game")
    void testCreateGamePVE() {

        UUID playerId = UUID.randomUUID();
        GameDto game = gameService.createGame(playerId, GameMode.PVE);

        assertNotNull(game.getId());

        GameDto found = gameService.findById(game.getId());
        assertNotNull(found);

        assertEquals(0, gameRepository.count(), "Active game should NOT be in MongoDB");
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

        UUID p1 = UUID.randomUUID();
        GameDto game = gameService.createGame(p1, GameMode.PVP);

        UUID p2 = UUID.randomUUID();
        gameService.joinGame(game.getId(), p2);

        assertEquals(0, gameRepository.count(), "Joined game should NOT be in MongoDB yet");
    }

    @Test
    @DisplayName("fial if player tries to join their own game")
    void testSelfJoinGame() {

        UUID player1 = UUID.randomUUID();
        GameDto game = gameService.createGame(player1, GameMode.PVP);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            gameService.joinGame(game.getId(), player1);
        });

        assertEquals("You are already in an active game.", exception.getMessage());
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

        gameService.makeMove(game.getId(), p1, new Move(new Position(5, 0), new Position(4, 1)));

        assertEquals(0, gameRepository.count(), "Game in progress should NOT be in MongoDB");
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

        assertEquals("It is currently Black's turn.",exception1.getMessage());
        assertEquals("It is currently Black's turn.",exception2.getMessage());
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

        assertEquals("Player is not a participant in this game.",exception.getMessage());

    }

    @Test
    @DisplayName("Test surrender")
    void testSurrender(){
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        GameDto game = gameService.createGame(p1, GameMode.PVP);
        gameService.joinGame(game.getId(), p2);

        gameService.surrenderGame(game.getId(), p1);

        assertEquals(1, gameRepository.count(), "Finished game MUST be in MongoDB");
    }

    @Test
    @DisplayName("sould list all open PVP games only")
    void testGetOpenGmaes(){
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        UUID p4 = UUID.randomUUID();

        gameService.createGame(p1, GameMode.PVP);
        gameService.createGame(p2, GameMode.PVE);

        GameDto fullGame =  gameService.createGame(p3, GameMode.PVP);
        gameService.joinGame(fullGame.getId(), p4);

        List<GameDto> openGames = gameService.getOpenGames();

        assertEquals(1, openGames.size());
        assertEquals(GameMode.PVP, openGames.get(0).getMode());
        assertEquals(p1, openGames.get(0).getPlayerBlackId());

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
