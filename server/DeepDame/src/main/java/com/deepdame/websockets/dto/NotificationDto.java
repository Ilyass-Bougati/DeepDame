package com.deepdame.websockets.dto;

import com.deepdame.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationDto(
        @NotNull UserDto user,
        @NotNull UUID gameId
) {
}
