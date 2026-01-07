package com.deepdame.config;

import com.deepdame.engine.core.model.GameState;
import com.deepdame.entity.mongo.GameDocument;
import com.deepdame.enums.GameMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RedisInfrastructureTest {

    @Autowired
    private RedisTemplate<String, GameDocument> gameRedisTemplate;

    @Test
    @DisplayName("Save and Retrieve a GameDocument from Redis")
    void testRedisSerialization(){
        UUID gameID = UUID.randomUUID();

        GameDocument orgDoc = GameDocument.builder()
                .id(gameID)
                .mode(GameMode.PVE)
                .playerBlackId(UUID.randomUUID())
                .gameState(new GameState(gameID))
                .build();

        String key = "test:game:" + gameID;

        gameRedisTemplate.opsForValue().set(key, orgDoc);

        GameDocument loadedDoc = gameRedisTemplate.opsForValue().get(key);

        assertNotNull(loadedDoc, "Redis should return the object");
        assertEquals(orgDoc.getId(), loadedDoc.getId());
        assertEquals(orgDoc.getMode(), loadedDoc.getMode());
        assertNotNull(loadedDoc.getGameState(), "Nested objects should be preserved");

        gameRedisTemplate.delete(key);
    }
}
