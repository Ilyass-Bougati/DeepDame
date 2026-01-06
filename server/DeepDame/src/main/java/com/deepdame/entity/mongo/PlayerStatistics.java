package com.deepdame.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "player_statistics")
public class PlayerStatistics {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID userId;

    @Builder.Default
    private long totalGamesPlayed = 0;
    @Builder.Default
    private long totalWins = 0;
    @Builder.Default
    private long totalLosses = 0;

    @Builder.Default
    private Map<String, MonthlyStat> monthlyStats = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> winsAgainstOpponent = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> gamesAgainstOpponent = new HashMap<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyStat {
        private int games;
        private int wins;
        private int losses;

        public void incrementWin() { this.games++; this.wins++; }
        public void incrementLoss() { this.games++; this.losses++; }
    }
}
