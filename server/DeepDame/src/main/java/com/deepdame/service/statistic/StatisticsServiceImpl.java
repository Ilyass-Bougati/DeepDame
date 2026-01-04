package com.deepdame.service.statistic;

import com.deepdame.repository.GameRepository;
import com.deepdame.repository.UserRepository;
import com.deepdame.service.cache.GameCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService{

    private final GameRepository gameRepository;
    private final GameCacheService gameCacheService;
    private final UserRepository userRepository;

    public Map<String, Object> getUserStats(UUID userId){

        Map<String, Object> stats = new HashMap<>();

        long total = gameRepository.countTotalGamesPlayed(userId);
        long wins = gameRepository.countByWinnerId(userId);
        long losses = total - wins;

        stats.put("totalGames", total);
        stats.put("totalWins", wins);
        stats.put("totalLosses", losses);
        stats.put("winRatioAllTime", calculateRatio(wins, total));

        stats.put("winRatioLastMonth", getWinRatioSince(userId, 1));
        stats.put("winRatioLast6Months", getWinRatioSince(userId, 6));
        stats.put("winRatioLastYear", getWinRatioSince(userId, 12));

        return stats;
    }

    public Map<String, Object> getFriendStats(UUID myId, UUID friendId) {
        long total = gameRepository.countGamesBetween(myId, friendId);
        long myWins = gameRepository.countWinsAgainst(myId, friendId);
        long friendWins = gameRepository.countWinsAgainst(friendId, myId);

        return Map.of(
                "totalMatches", total,
                "myWins", myWins,
                "friendWins", friendWins
        );
    }

    public Map<String, Object> getGlobalStats(){

        Map<String, Object> globalStats = new HashMap<>();

        globalStats.put("totalUsers", userRepository.count());
        globalStats.put("totalNewUsersToday", userRepository.countNewUsersToday());
        globalStats.put("activeUsers", userRepository.countByBannedFromAppFalse());
        globalStats.put("bannedUsers", userRepository.countByBannedFromAppTrue());

        globalStats.put("activeLobbyGames", gameCacheService.getOpenGames().size());

        globalStats.put("totalGamesFinished", gameRepository.count());

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        globalStats.put("gamesLast30Days", gameRepository.getGamesPerDay(thirtyDaysAgo));

        return globalStats;
    }

    private double calculateRatio(long wins, long total) {
        if (total == 0) return 0.0;
        return Math.round((double) wins / total * 100.0) / 100.0;
    }

    private double getWinRatioSince(UUID userId, int months) {
        LocalDateTime sinceDate = LocalDateTime.now().minusMonths(months);
        long total = gameRepository.countGamesSince(userId, sinceDate);
        long wins = gameRepository.countWinsSince(userId, sinceDate);
        return calculateRatio(wins, total);
    }
}
