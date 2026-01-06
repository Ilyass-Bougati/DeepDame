package com.deepdame.stats;

import com.deepdame.entity.mongo.GameDocument;
import com.deepdame.entity.User;
import com.deepdame.enums.GameMode;
import com.deepdame.repository.mongo.GameRepository;
import com.deepdame.repository.UserRepository;
import com.deepdame.service.cache.GameCacheService;
import com.deepdame.service.statistic.StatisticsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StatsServiceTest {

    @Autowired
    private StatisticsServiceImpl statisticsService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameCacheService gameCacheService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private User hmed;
    private User bochaab;

    @BeforeEach
    void setUp() {
        cleanAllDatabases();

        hmed = User.builder()
                .username("hmed").email("hmed@test.com").password("pass")
                .bannedFromApp(false).emailValidated(true)
                .build();

        bochaab = User.builder()
                .username("bochaab").email("bochaab@test.com").password("pass")
                .bannedFromApp(false).emailValidated(true)
                .build();

        userRepository.saveAll(List.of(hmed, bochaab));

        // hmed Wins 1
        createFinishedGame(hmed.getId(), bochaab.getId(), hmed.getId(), LocalDateTime.now());
        // hmed Loses 1 (bochaab Wins)
        createFinishedGame(hmed.getId(), bochaab.getId(), bochaab.getId(), LocalDateTime.now().minusDays(1));

        createActiveGameInRedis();
        createActiveGameInRedis();
        createActiveGameInRedis();
    }

    @AfterEach
    void tearDown() {
        cleanAllDatabases();
    }

    private void cleanAllDatabases() {
        userRepository.deleteAll();
        gameRepository.deleteAll();

        deleteKeys("game:*");
        deleteKeys("lobby:*");
        deleteKeys("user:*");
    }

    private void deleteKeys(String pattern) {
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    @DisplayName("GLOBAL: Should return dashboard stats with Chart Data")
    void testGlobalStats() {
        Map<String, Object> global = statisticsService.getGlobalStats();

        printJson("Global Stats", global);

        assertEquals(2L, global.get("totalUsers"));
        assertEquals(2L, global.get("totalGamesFinished"));
        assertEquals(3, global.get("activeLobbyGames"));

        List<Map<String, Object>> chart = (List<Map<String, Object>>) global.get("gamesLast30Days");
        assertNotNull(chart);
        assertFalse(chart.isEmpty());
        assertNotNull(chart.get(0).get("date"));
    }

    @Test
    @DisplayName("USER: Should calculate Win/Loss ratios correctly")
    void testUserStats() {
        Map<String, Object> heroStats = statisticsService.getUserStats(hmed.getId());

        printJson("User Stats (hmed)", heroStats);

        // hmed played 2 games total
        assertEquals(2L, heroStats.get("totalGames"));

        // hmed won 1
        assertEquals(1L, heroStats.get("totalWins"));

        // hmed lost 1
        assertEquals(1L, heroStats.get("totalLosses"));

        // Ratio should be 0.5 (50%)
        assertEquals(0.5, heroStats.get("winRatioAllTime"));
    }

    @Test
    @DisplayName("FRIEND: Should calculate Head-to-Head stats correctly")
    void testFriendStats() {
        // hmed checks stats against bochaab
        Map<String, Object> friendStats = statisticsService.getFriendStats(hmed.getId(), bochaab.getId());

        printJson("Friend Stats (hmed vs bochaab)", friendStats);

        // They played 2 games in setUp()
        assertEquals(2L, friendStats.get("totalMatches"));

        // hmed won the 1st game
        assertEquals(1L, friendStats.get("myWins"));

        // bochaab won the 2nd game
        assertEquals(1L, friendStats.get("friendWins"));
    }

    private void printJson(String title, Object object) {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println("\n=== " + title + " ===");
            System.out.println(json);
            System.out.println("====================\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFinishedGame(UUID p1, UUID p2, UUID winner, LocalDateTime date) {
        GameDocument game = GameDocument.builder()
                .id(UUID.randomUUID())
                .mode(GameMode.PVP)
                .playerBlackId(p1)
                .playerWhiteId(p2)
                .winnerId(winner)
                .gameDate(date)
                .build();
        gameRepository.save(game);
    }

    private void createActiveGameInRedis() {
        UUID gameId = UUID.randomUUID();
        GameDocument activeGame = GameDocument.builder()
                .id(gameId)
                .mode(GameMode.PVP)
                .playerBlackId(hmed.getId())
                .build();

        gameCacheService.saveGame(activeGame);
        gameCacheService.addToLobby(gameId);
    }
}
