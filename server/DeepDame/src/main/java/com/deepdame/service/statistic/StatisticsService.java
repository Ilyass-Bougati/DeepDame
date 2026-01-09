package com.deepdame.service.statistic;

import com.deepdame.dto.stats.PlayerStatsDto;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface StatisticsService {
    void updateStatsAfterGame(UUID winnerId, UUID loserId);

    PlayerStatsDto getPlayerStats(UUID id);
}
