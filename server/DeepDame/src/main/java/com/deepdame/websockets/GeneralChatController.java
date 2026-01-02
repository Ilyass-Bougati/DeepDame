package com.deepdame.websockets;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.generalChatMessage.GeneralChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @MessageMapping("/message")
    @SendTo("/topic/general-chat")
    public GeneralChatMessageDto greeting(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @Payload GeneralChatMessageDto message
    ) {
        message.setUserId(principal.getUser().getId());
        message.setId(null);
        return generalChatMessageService.save(message);
    }
}
