package com.deepdame.websockets;

import com.deepdame.exception.WsUnauthorized;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.cache.RedisNotificationService;
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
    private final RedisNotificationService redisNotificationService;

    /**
     * As you'd notice this controller sends a message to the websocket `/topic/sender/userId/notification`
     * hence for a sender to get a notification, they'll have to be subscribed to that topic
     * @param principal this will be inserted directly by Spring Security
     * @param request this project holds the details of the notification
     */
    @MessageMapping("/invite/game")
    public void inviteToGame(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @Payload GameInvitationDto request
    ) {
        log.debug("Received game invitation for sender {}", request.userId());

        if (principal.getUser().getId().equals(request.userId())) {
            throw new WsUnauthorized("You can only invite yourself");
        }

        // Checking if the two are friends
        if (!userService.areFriends(principal.getUser().getId(), request.userId())) {
            throw new WsUnauthorized("You can only invite your friends");
        }

        // Formulating a response
        GameNotificationDto notification = GameNotificationDto.builder()
                .sender(principal.getUser())
                .receiverId(request.userId())
                .gameId(request.gameId())
                .build();

        redisNotificationService.sendMessage(notification, "game-invitation");
    }

    @MessageMapping("/invite/friend/{userId}")
    public void friendInvitation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @DestinationVariable UUID userId
    ) {
        log.debug("Sending friend invitation to {}", userId);

        if (principal.getUser().getId().equals(userId)) {
            throw new WsUnauthorized("You can't invite youself");
        }

        // Checking if the two are friends
        if (userService.areFriends(principal.getUser().getId(), userId)) {
            throw new WsUnauthorized("You can't invite your friends");
        }

        if (friendRequestService.friendRequestExists(principal.getUser().getId(), userId)) {
            throw new WsUnauthorized("You can't invite this person");
        }

        friendRequestService.addFriendRequest(principal.getUser().getId(), userId);

        redisNotificationService.sendMessage(FriendRequestDto.builder().receiverId(userId).senderId(principal.getUser().getId()).build(), "friend-invitation");
    }
}
