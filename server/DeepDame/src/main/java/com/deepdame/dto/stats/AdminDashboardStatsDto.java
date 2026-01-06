package com.deepdame.dto.stats;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

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

    private Map<LocalDate, Long> gamesPerDayLast30Days;
}
