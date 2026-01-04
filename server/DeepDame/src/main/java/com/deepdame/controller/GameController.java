package com.deepdame.controller;

import com.deepdame.dto.game.GameDto;
import com.deepdame.service.game.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/lobby")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/open")
    public ResponseEntity<List<UUID>> getOpenGames() {

        List<GameDto> openGames = gameService.getOpenGames();

        List<UUID> openGamesIds = openGames.stream()
                .map(GameDto::getId)
                .toList();

        log.info("Lobby: Fetched {} open games", openGamesIds.size());

        return ResponseEntity.ok(openGamesIds);
    }
}
