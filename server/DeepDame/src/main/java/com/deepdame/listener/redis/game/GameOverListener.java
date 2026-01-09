package com.deepdame.listener.redis.game;

import com.deepdame.dto.redis.GameOverMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameOverListener implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        log.debug("Game over received: {}", message);
        GameOverMessage gameOverMessage = objectMapper.readValue(message.getBody(), GameOverMessage.class);
        messagingTemplate.convertAndSend("/topic/game/" + gameOverMessage.gameId() + "/game-over", gameOverMessage);
    }
}