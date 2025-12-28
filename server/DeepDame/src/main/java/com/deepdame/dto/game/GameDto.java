package com.deepdame.dto.game;

import com.deepdame.engine.core.model.GameState;
import com.deepdame.enums.GameMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {

    private UUID id;

    private GameState gameState;

    private GameMode mode;
    private UUID playerBlackId;
    private UUID playerWhiteId;
}
