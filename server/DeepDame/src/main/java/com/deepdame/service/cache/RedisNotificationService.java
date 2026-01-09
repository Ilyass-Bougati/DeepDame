package com.deepdame.service.cache;

import com.deepdame.exception.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
public class RedisNotificationService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> void sendMessage(T payload, String channel) {
        try {
            String jsonEntry = objectMapper.writeValueAsString(payload);
            redisTemplate.convertAndSend(channel, jsonEntry);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
}
