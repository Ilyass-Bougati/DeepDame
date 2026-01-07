package com.deepdame.service.statistic;

import com.deepdame.dto.stats.AdminDashboardStatsDto;
import com.deepdame.dto.stats.PlayerStatsDto;
import com.deepdame.entity.mongo.PlayerStatistics;
import com.deepdame.repository.mongo.GameRepository;
import com.deepdame.repository.UserRepository;
import com.deepdame.repository.mongo.PlayerStatisticsRepository;
import com.deepdame.service.cache.GameCacheService;
import com.deepdame.listeners.WebSocketPresenceEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final GameCacheService gameCacheService;
    private final StringRedisTemplate stringRedisTemplate;

    public AdminDashboardStatsDto getAdminDashboardStats() {

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        long onlineCount = 0;
        try {
            Long size = stringRedisTemplate.opsForSet().size(WebSocketPresenceEventListener.KEY_ONLINE_USERS);
            onlineCount = (size != null) ? size : 0;
        } catch (Exception e) { /**/}

        List<Map<String, Object>> rawData = gameRepository.getGamesPerDay(thirtyDaysAgo);

        Map<LocalDate, Long> chartMap = rawData.stream()
                .collect(Collectors.toMap(
                        entry -> LocalDate.parse((String) entry.get("date")),

                        entry -> ((Number) entry.get("count")).longValue(),

                        (existing, replacement) -> existing
                ));

        Map<LocalDate, Long> continuousMap = new TreeMap<>();

        for (int i = 0; i < 30; i++) {
            LocalDate date = LocalDate.now().minusDays(29 - i);
            continuousMap.put(date, chartMap.getOrDefault(date, 0L));
        }

        List<String> days = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        continuousMap.forEach((date, count) -> {
            days.add(date.toString());
            counts.add(count);
        });

        log.debug("the chart data = {}", continuousMap);

        return AdminDashboardStatsDto.builder()
                .totalUsers(userRepository.count())
                .newUsersToday(userRepository.countByCreatedAtAfter(startOfDay))
                .activeUsers(userRepository.countByBannedFromAppFalse())
                .bannedUsers(userRepository.countByBannedFromAppTrue())
                .onlinePlayers(onlineCount)
                .activeLobbyGames(gameCacheService.getOpenGames().size())
                .totalGamesFinished(gameRepository.count())
                .chartDays(days)
                .chartCounts(counts)
                .build();
    }

    public PlayerStatsDto getPlayerStats(UUID userId) {

        PlayerStatistics stats = playerStatisticsRepository.findByUserId(userId)
                .orElse(PlayerStatistics.builder().userId(userId).build());

        return PlayerStatsDto.builder()
                .totalPlayed(stats.getTotalGamesPlayed())
                .totalWins(stats.getTotalWins())
                .totalLosses(stats.getTotalLosses())
                .winRatioAllTime(calculateRatio(stats.getTotalWins(), stats.getTotalGamesPlayed()))
                .winRatioLastMonth(calculateTimeBasedRatio(stats, 1))
                .winRatioLast6Months(calculateTimeBasedRatio(stats, 6))
                .winRatioLastYear(calculateTimeBasedRatio(stats, 12))
                .build();
    }

    @Async("taskExecutor")
    public void updateStatsAfterGame(UUID winnerId, UUID loserId) {
        if (winnerId != null) updateOneUser(winnerId, true, false, loserId);
        if (loserId != null) updateOneUser(loserId, false, true, winnerId);
    }

    private void updateOneUser(UUID userId, boolean isWin, boolean isLoss, UUID opponentId) {

        PlayerStatistics stats = playerStatisticsRepository.findByUserId(userId)
                .orElse(PlayerStatistics.builder().userId(userId).build());

        stats.setTotalGamesPlayed(stats.getTotalGamesPlayed() + 1);
        if (isWin) stats.setTotalWins(stats.getTotalWins() + 1);
        if (isLoss) stats.setTotalLosses(stats.getTotalLosses() + 1);

        String monthKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        PlayerStatistics.MonthlyStat monthStat = stats.getMonthlyStats()
                .getOrDefault(monthKey, new PlayerStatistics.MonthlyStat());

        if (isWin) monthStat.incrementWin();
        else if (isLoss) monthStat.incrementLoss();

        stats.getMonthlyStats().put(monthKey, monthStat);

        if (opponentId != null) {
            String oppKey = opponentId.toString();
            stats.getGamesAgainstOpponent().merge(oppKey, 1, Integer::sum);
            if (isWin) stats.getWinsAgainstOpponent().merge(oppKey, 1, Integer::sum);
        }

        playerStatisticsRepository.save(stats);
    }

    private double calculateRatio(long wins, long total) {
        if (total == 0) return 0.0;
        return Math.round((double) wins / total * 100.0) / 100.0;
    }

    private double calculateTimeBasedRatio(PlayerStatistics stats, int monthsBack) {
        LocalDate now = LocalDate.now();
        long wins = 0;
        long total = 0;

        for (int i = 0; i < monthsBack; i++) {
            String key = now.minusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM"));
            PlayerStatistics.MonthlyStat m = stats.getMonthlyStats().get(key);
            if (m != null) {
                wins += m.getWins();
                total += m.getGames();
            }
        }
        return calculateRatio(wins, total);
    }
}