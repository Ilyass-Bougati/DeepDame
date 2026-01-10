package com.deepdame.listener.redis.notification;

import com.deepdame.websockets.dto.GameNotificationDto;
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
public class GameInvitationListener implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        log.debug("Game invitation received: {}", message);
        GameNotificationDto gameInvitation = objectMapper.readValue(message.getBody(), GameNotificationDto.class);
        messagingTemplate.convertAndSend("/topic/user/" + gameInvitation.receiverId() + "/game-notifications", gameInvitation);
    }
}