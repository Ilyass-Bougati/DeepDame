package com.deepdame.websockets;

import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.friendRequest.FriendRequestService;
import com.deepdame.service.user.UserService;
import com.deepdame.websockets.dto.FriendRequestDto;
import com.deepdame.websockets.dto.GameNotificationDto;
import com.deepdame.websockets.dto.GameInvitationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final FriendRequestService friendRequestService;

    /**
     * As you'd notice this controller sends a message to the websocket `/topic/user/userId/notification`
     * hence for a user to get a notification, they'll have to be subscribed to that topic
     * @param principal this will be inserted directly by Spring Security
     * @param request this project holds the details of the notification
     */
    @MessageMapping("/invite/game")
    public void inviteToGame(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @Payload GameInvitationDto request
    ) {
        log.debug("Received game invitation for user {}", request.userId());

        // Checking if the two are friends
        if (!userService.areFriends(principal.getUser().getId(), request.userId())) {
            // TODO : Handle this error
            return;
        }

        // Formulating a response
        GameNotificationDto notification = GameNotificationDto.builder()
                .user(principal.getUser())
                .gameId(request.gameId())
                .build();

        String destination = "/topic/user/" + request.userId() + "/game-notifications";
        messagingTemplate.convertAndSend(destination, notification);
    }

    @MessageMapping("/invite/friend/{userId}")
    public void friendInvitation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @DestinationVariable UUID userId
    ) {
        log.debug("Sending friend invitation to {}", userId);

        // Checking if the two are friends
        if (!userService.areFriends(principal.getUser().getId(), userId)) {
            // TODO : Handle this error
            return;
        }

        friendRequestService.addFriendRequest(principal.getUser().getId(), userId);

        String destination = "/topic/user/" + userId + "/friend-request-notifications";
        messagingTemplate.convertAndSend(destination, FriendRequestDto.builder().userId(userId).build());
    }
}
