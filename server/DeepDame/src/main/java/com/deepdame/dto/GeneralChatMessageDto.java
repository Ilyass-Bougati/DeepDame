package com.deepdame.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GeneralChatMessageDto {
    @EqualsAndHashCode.Include
    private UUID id;

    @NotEmpty
    private String message;

    @NotNull
    private LocalDateTime createdAt;
}
