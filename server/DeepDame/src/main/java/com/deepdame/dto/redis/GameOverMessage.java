package com.deepdame.dto.redis;

import java.util.UUID;

public record GameOverMessage(String winnerColor, String winnerName, UUID winnerId, UUID gameId) {
}
