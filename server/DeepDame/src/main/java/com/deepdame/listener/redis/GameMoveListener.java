package com.deepdame.listener.redis;

import com.deepdame.dto.redis.GameMoveMessageDto;
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
public class GameMoveListener implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        log.debug("Game move received: {}", message);
        GameMoveMessageDto move = objectMapper.readValue(message.getBody(), GameMoveMessageDto.class);
        messagingTemplate.convertAndSend("/topic/game/" + move.getGameId(), move.getMove());
    }
}
