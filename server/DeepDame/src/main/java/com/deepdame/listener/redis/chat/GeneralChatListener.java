package com.deepdame.listener.redis.chat;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.dto.redis.GameChatMessage;
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
public class GeneralChatListener implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        log.debug("General chat received: {}", message);
        GeneralChatMessageDto chatMessage = objectMapper.readValue(message.getBody(), GeneralChatMessageDto.class);
        messagingTemplate.convertAndSend("/topic/general-chat", chatMessage);
    }
}
