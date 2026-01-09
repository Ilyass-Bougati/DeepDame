package com.deepdame.stats;

import com.deepdame.dto.stats.AdminDashboardStatsDto;
import com.deepdame.dto.stats.PlayerStatsDto;
import com.deepdame.entity.mongo.GameDocument;
import com.deepdame.entity.User;
import com.deepdame.enums.GameMode;
import com.deepdame.repository.mongo.GameRepository;
import com.deepdame.repository.UserRepository;
import com.deepdame.repository.mongo.PlayerStatisticsRepository;
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
    private PlayerStatisticsRepository playerStatisticsRepository;

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

        // 1. Create Users
        hmed = User.builder()
                .username("hmed").email("hmed@test.com").password("pass")
                .bannedFromApp(false).emailValidated(true)
                .build();

        bochaab = User.builder()
                .username("bochaab").email("bochaab@test.com").password("pass")
                .bannedFromApp(false).emailValidated(true)
                .build();

        userRepository.saveAll(List.of(hmed, bochaab));

        // 2. Create Games AND Trigger Stat Updates
        // Game 1: Hmed Wins
        createFinishedGame(hmed.getId(), bochaab.getId(), hmed.getId(), LocalDateTime.now());
        statisticsService.updateStatsAfterGame(hmed.getId(), bochaab.getId());

        // Game 2: Bochaab Wins (Hmed Loses)
        createFinishedGame(hmed.getId(), bochaab.getId(), bochaab.getId(), LocalDateTime.now().minusDays(1));
        statisticsService.updateStatsAfterGame(bochaab.getId(), hmed.getId());

        // 3. Create Active Games (Lobby)
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
        playerStatisticsRepository.deleteAll(); // Clean stats too

        deleteKeys("game:*");
        deleteKeys("lobby:*");
        deleteKeys("sender:*");
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

        AdminDashboardStatsDto global = statisticsService.getAdminDashboardStats();

        printJson("Global Stats DTO", global);

        // Assert Basic Counts
        assertEquals(2L, global.getTotalUsers());
        assertEquals(2L, global.getTotalGamesFinished());
        assertEquals(3, global.getActiveLobbyGames());

        // Assert Chart Data (Lists)
        assertNotNull(global.getChartDays());
        assertNotNull(global.getChartCounts());
        assertEquals(30, global.getChartDays().size()); // Should always have 30 days
        assertEquals(30, global.getChartCounts().size());

        // Check if data is populated correctly (Total 2 games in the counts)
        long totalGamesInChart = global.getChartCounts().stream().mapToLong(Long::longValue).sum();
        assertEquals(2L, totalGamesInChart, "Chart should contain exactly 2 games across all days");
    }

    @Test
    @DisplayName("USER: Should calculate Win/Loss ratios correctly")
//    void testUserStats() {
//
//        PlayerStatsDto heroStats = statisticsService.getPlayerStats(hmed.getId());
//
//        printJson("Player Stats (hmed)", heroStats);
//
//        // hmed played 2 games total
//        assertEquals(2L, heroStats.getTotalPlayed());
//
//        // hmed won 1
//        assertEquals(1L, heroStats.getTotalWins());
//
//        // hmed lost 1
//        assertEquals(1L, heroStats.getTotalLosses());
//
//        // Ratio should be 0.5 (50%)
//        assertEquals(0.5, heroStats.getWinRatioAllTime());
//
//        // Check Last Month Ratio (Should also be 0.5 since games are recent)
//        assertEquals(0.5, heroStats.getWinRatioLastMonth());
//    }

    // Helper methods
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