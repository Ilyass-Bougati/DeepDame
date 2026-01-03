package com.deepdame.dto.game;

import com.deepdame.engine.core.model.GameState;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.GameMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {

    private UUID id;

    private GameMode mode;
    private UUID playerBlackId;
    private UUID playerWhiteId;

    private UUID winnerId;

    private LocalDateTime gameDate;

    private GameState gameState;

    private List<Move> history;
}
