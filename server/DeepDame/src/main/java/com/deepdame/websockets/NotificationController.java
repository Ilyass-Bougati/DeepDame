package com.deepdame.websockets;

import com.deepdame.dto.generalChatMessage.ChatUserData;
import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.websockets.dto.NotificationDto;
import com.deepdame.websockets.dto.NotificationRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * As you'd notice this controller sends a message to the websocket `/topic/user/userId/notification`
     * hence for a user to get a notification, they'll have to be subscribed to that topic
     * @param principal this will be inserted directly by Spring Security
     * @param userId This is the id of the user we're sending notifications to
     */
    @MessageMapping("/invite")
    public void greeting(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @Payload NotificationRequestDto request
    ) {
        log.debug("Received greeting request for user {}", request.userId());
        // Formulating a response
        NotificationDto notification = NotificationDto.builder()
                .user(principal.getUser())
                .gameId(request.gameId())
                .build();

        String destination = "/topic/user/" + request.userId() + "/notifications";
        messagingTemplate.convertAndSend(destination, notification);
    }
}
