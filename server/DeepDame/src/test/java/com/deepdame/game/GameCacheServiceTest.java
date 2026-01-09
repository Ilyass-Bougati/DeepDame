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
        if (stringTemplate.getConnectionFactory() != null) {
            Objects.requireNonNull(stringTemplate.getConnectionFactory())
                    .getConnection()
                    .serverCommands()
                    .flushAll();
        }
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

        createAndSaveGame(openGame1);
        createAndSaveGame(openGame2);
        createAndSaveGame(openGame3);

        gameCacheService.addToLobby(openGame1);
        gameCacheService.addToLobby(openGame2);
        gameCacheService.addToLobby(openGame3);

        List<GameDocument> openGames = gameCacheService.getOpenGames();

        assertEquals(3, openGames.size());

        gameCacheService.removeFromLobby(openGame1);
        gameCacheService.removeFromLobby(openGame2);
        gameCacheService.removeFromLobby(openGame3);

        assertEquals(0, gameCacheService.getOpenGames().size());
    }

    @Test
    @DisplayName("Test auto-clean stale IDs from lobby")
    public void lobbySelfHealingTest() {
        UUID activeGameId = UUID.randomUUID();
        UUID staleGameId = UUID.randomUUID();

        createAndSaveGame(activeGameId);

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

        createAndSaveGame(gameId);

        assertFalse(gameCacheService.isUserPlaying(userId), "User should not be playing initially");

        gameCacheService.setUserCurrentGame(userId, gameId);

        assertTrue(gameCacheService.isUserPlaying(userId), "User should be marked as playing");
        assertEquals(gameId, gameCacheService.getUserCurrentGameId(userId), "Retrieved Game ID should match");

        gameCacheService.clearUserCurrentGame(userId);

        assertFalse(gameCacheService.isUserPlaying(userId), "User should not be playing after clear");
        assertNull(gameCacheService.getUserCurrentGameId(userId), "Game ID should be null after clear");
    }

    @Test
    @DisplayName("Zombie Session: User key exists but Game expired")
    public void testZombieSessionCleanup() {
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        gameCacheService.setUserCurrentGame(userId, gameId);

        assertNotNull(gameCacheService.getUserCurrentGameId(userId), "User key should technically exist in Redis before check");

        boolean isPlaying = gameCacheService.isUserPlaying(userId);

        assertFalse(isPlaying, "Should return false because game object is missing");

        assertNull(gameCacheService.getUserCurrentGameId(userId), "User key should have been auto-deleted");
    }

    private void createAndSaveGame(UUID id) {
        GameDocument game = GameDocument.builder()
                .id(id)
                .mode(GameMode.PVP)
                .gameState(new GameState(id))
                .playerBlackId(UUID.randomUUID())
                .build();
        gameCacheService.saveGame(game);
    }
}