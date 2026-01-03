package com.deepdame.game;


import com.deepdame.dto.game.GameDto;
import com.deepdame.dto.game.GameMapper;
import com.deepdame.engine.core.model.*;
import com.deepdame.entity.GameDocument;
import com.deepdame.enums.GameMode;
import com.deepdame.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GameMapperTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameMapper gameMapper;

    @Test
    public void testSaveAndLoadGmae(){
        UUID gameId = UUID.randomUUID();
        UUID playerWhiteId = UUID.randomUUID();
        UUID playerBlackId = UUID.randomUUID();

        GameState gameState = new GameState(gameId);

        GameDocument doc = GameDocument.builder()
                                .id(gameId)
                                .gameState(gameState)
                                .mode(GameMode.PVP)
                                .playerBlackId(playerBlackId)
                                .playerWhiteId(playerWhiteId)
                                .build();

        System.out.println("----------- Saving game------/;'");
        gameRepository.save(doc);

        System.out.println("---------------- Loading game------------");
        GameDocument loadedDoc = gameRepository.findById(gameId).orElse(null);

        assertNotNull(loadedDoc, "Game should be found in DB");
        assertEquals(gameId, loadedDoc.getId(), "IDs should match");
        assertNotNull(loadedDoc.getGameState(), "GameState object should be nested");
        assertNotNull(loadedDoc.getGameState().getBoard(), "Board should be loaded");

        loadedDoc.getGameState().getBoard().printBoard();

        GameDto dto = gameMapper.toDTO(loadedDoc);
        assertNotNull(dto.getGameState(), "DTO should contain the GameState");
        assertEquals(gameId, dto.getId(), "DTO ID should match");

    }

}
