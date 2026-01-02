package com.deepdame.entity;

import com.deepdame.engine.core.model.GameState;
import com.deepdame.enums.GameMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class GameDocument {

    @Id
    private UUID id;

    private GameState gameState;

    private GameMode mode;
    private UUID playerBlackId;
    private UUID playerWhiteId;
}



