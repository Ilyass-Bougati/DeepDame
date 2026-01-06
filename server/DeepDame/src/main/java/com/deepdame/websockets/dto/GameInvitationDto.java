package com.deepdame.websockets.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GameInvitationDto(
        @NotNull UUID userId,
        @NotNull UUID gameId
) {
}
