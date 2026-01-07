package com.deepdame.dto.stats;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminDashboardStatsDto {
    private long totalUsers;
    private long newUsersToday;
    private long activeUsers;
    private long bannedUsers;

    private long onlinePlayers;
    private int activeLobbyGames;
    private long totalGamesFinished;

    private List<String> chartDays;
    private List<Long> chartCounts;
}
