package com.deepdame.game;

import com.deepdame.engine.core.model.GameState;
import com.deepdame.entity.mongo.GameDocument;
import com.deepdame.enums.GameMode;
import com.deepdame.service.cache.GameCacheService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameCacheServiceTest {

    @Autowired
    private GameCacheService gameCacheService;

    @Autowired
    private StringRedisTemplate stringTemplate;


    @BeforeEach
    public void beforeTest(){
        cleanRedis();
    }

    @AfterEach
    public void afterAllTests(){
        cleanRedis();
    }

    private void cleanRedis() {

        Objects.requireNonNull(stringTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Test
    @DisplayName("Add Get Remove game from chache")
    public void gameCacheCrudOpsTest(){
        UUID gameId = UUID.randomUUID();

        GameDocument newGmae = GameDocument.builder()
                .id(gameId)
                .mode(GameMode.PVP)
                .playerBlackId(UUID.randomUUID())
                .gameState(new GameState(gameId))
                .build();

        gameCacheService.saveGame(newGmae);

        GameDocument loadedGame = gameCacheService.getGame(gameId);

        assertNotNull(loadedGame, "Redis should return the object");
        assertEquals(newGmae.getId(), loadedGame.getId());
        assertEquals(newGmae.getMode(), loadedGame.getMode());
        assertNotNull(loadedGame.getGameState(), "Nested objects should be preserved");

        gameCacheService.deleteGame(gameId);
        assertNull(gameCacheService.getGame(gameId), "Redis should not return the object");
    }

    @Test
    @DisplayName("Should add remove open games Lobby and list all open Games")
    public void gameLobbyCacheTest(){

        UUID openGame1 = UUID.randomUUID();
        UUID openGame2 = UUID.randomUUID();
        UUID openGame3 = UUID.randomUUID();

        GameDocument og1 = GameDocument.builder().id(openGame1).mode(GameMode.PVP).gameState(new GameState(openGame1)).playerBlackId(UUID.randomUUID()).build();
        GameDocument og2 = GameDocument.builder().id(openGame2).mode(GameMode.PVP).gameState(new GameState(openGame2)).playerBlackId(UUID.randomUUID()).build();
        GameDocument og3 = GameDocument.builder().id(openGame3).mode(GameMode.PVP).gameState(new GameState(openGame3)).playerBlackId(UUID.randomUUID()).build();

        gameCacheService.saveGame(og1);
        gameCacheService.saveGame(og2);
        gameCacheService.saveGame(og3);

        gameCacheService.addToLobby(og1.getId());
        gameCacheService.addToLobby(og2.getId());
        gameCacheService.addToLobby(og3.getId());

        List<GameDocument> openGames = gameCacheService.getOpenGames();

        System.out.println(openGames);


        assertEquals(3, openGames.toArray().length);

        gameCacheService.removeFromLobby(og1.getId());
        gameCacheService.removeFromLobby(og2.getId());
        gameCacheService.removeFromLobby(og3.getId());

        assertEquals(0, gameCacheService.getOpenGames().toArray().length);
    }

    @Test
    @DisplayName("Test auto-clean stale IDs from lobby")
    public void lobbySelfHealingTest() {
        UUID activeGameId = UUID.randomUUID();
        UUID staleGameId = UUID.randomUUID();

        GameDocument activeGame = GameDocument.builder()
                .id(activeGameId)
                .mode(GameMode.PVP)
                .gameState(new GameState(activeGameId))
                .playerBlackId(UUID.randomUUID())
                .build();
        gameCacheService.saveGame(activeGame);

        gameCacheService.addToLobby(activeGameId);
        gameCacheService.addToLobby(staleGameId);

        List<GameDocument> result = gameCacheService.getOpenGames();

        assertEquals(1, result.size(), "Should return only the valid game");
        assertEquals(activeGameId, result.get(0).getId());

        List<GameDocument> resultAfterClean = gameCacheService.getOpenGames();
        assertEquals(1, resultAfterClean.size());
    }

    @Test
    @DisplayName("manage user active game session")
    public void userGameSessionTest() {
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        assertFalse(gameCacheService.isUserPlaying(userId), "User should not be playing initially");
        assertNull(gameCacheService.getUserCurrentGameId(userId), "Game ID should be null initially");

        gameCacheService.setUserCurrentGame(userId, gameId);

        assertTrue(gameCacheService.isUserPlaying(userId), "User should be marked as playing");
        assertEquals(gameId, gameCacheService.getUserCurrentGameId(userId), "Retrieved Game ID should match the one set");

        UUID secondGameId = UUID.randomUUID();
        gameCacheService.setUserCurrentGame(userId, secondGameId);

        assertEquals(secondGameId, gameCacheService.getUserCurrentGameId(userId), "Should update to the new Game ID correctly");

        gameCacheService.clearUserCurrentGame(userId);

        assertFalse(gameCacheService.isUserPlaying(userId), "User should not be playing after clear");
        assertNull(gameCacheService.getUserCurrentGameId(userId), "Game ID should be null after clear");
    }
}
