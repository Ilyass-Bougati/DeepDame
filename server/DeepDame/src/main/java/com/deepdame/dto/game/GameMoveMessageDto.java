package com.deepdame.dto.game;

import com.deepdame.engine.core.model.Move;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameMoveMessageDto {
    private UUID gameId;
    private Move move;
}
