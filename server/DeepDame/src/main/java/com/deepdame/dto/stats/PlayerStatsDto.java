package com.deepdame.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerStatsDto {

    private long totalPlayed;
    private long totalWins;
    private long totalLosses;

    private double winRatioAllTime;
    private double winRatioLastMonth;
    private double winRatioLast6Months;
    private double winRatioLastYear;
}