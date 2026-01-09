package com.deepdame.controller;

import com.deepdame.dto.stats.PlayerStatsDto;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.statistic.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatisticsService statisticsService;

    @GetMapping("/me")
    public ResponseEntity<PlayerStatsDto> getMyStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        UUID userId = userDetails.getUser().getId();

        return ResponseEntity.ok(statisticsService.getPlayerStats(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PlayerStatsDto> getPlayerStats(@PathVariable UUID userId) {
        return ResponseEntity.ok(statisticsService.getPlayerStats(userId));
    }
}
