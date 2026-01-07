package com.deepdame.service.statistic;

import java.util.UUID;

public interface StatisticsService {
    void updateStatsAfterGame(UUID winnerId, UUID loserId);
}
