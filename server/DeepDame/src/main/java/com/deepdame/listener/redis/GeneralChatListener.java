package com.deepdame.listener.redis;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.sefault.redis.annotation.RedisListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralChatListener {
    private final SimpMessagingTemplate messagingTemplate;

    @RedisListener(topic = "general-chat")
    public void generalMessageHandler(GeneralChatMessageDto message) {
        messagingTemplate.convertAndSend("/topic/general-chat", message);
    }
}
