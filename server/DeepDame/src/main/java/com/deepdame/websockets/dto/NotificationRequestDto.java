package com.deepdame.websockets.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NotificationRequestDto(
        @NotNull UUID userId,
        @NotNull UUID gameId
) {
}
