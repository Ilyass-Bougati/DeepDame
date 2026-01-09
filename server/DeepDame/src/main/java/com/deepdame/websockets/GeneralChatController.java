package com.deepdame.websockets;

import com.deepdame.dto.generalChatMessage.ChatUserData;
import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.cache.RedisNotificationService;
import com.deepdame.service.generalChatMessage.GeneralChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GeneralChatController {
    private final GeneralChatMessageService generalChatMessageService;
    private final RedisNotificationService redisNotificationService;

    @MessageMapping("/message")
    public void greeting(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @Payload GeneralChatMessageDto message
    ) {
        message.setUser(ChatUserData.builder().id(principal.getUser().getId()).build());
        message.setId(null);

        GeneralChatMessageDto msg = generalChatMessageService.save(message);

        // broadcasting the message to all servers
        redisNotificationService.sendMessage(msg, "general-chat");
    }
}
