package com.deepdame.entity.mongo;

import com.deepdame.engine.core.model.GameState;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.GameMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class GameDocument {

    @Id
    private UUID id;

    private GameMode mode;
    private UUID playerBlackId;
    private UUID playerWhiteId;

    private UUID winnerId;

    @Builder.Default
    private LocalDateTime gameDate = LocalDateTime.now();

    private GameState gameState;

    @Builder.Default
    private List<Move> history = new ArrayList<>();
}