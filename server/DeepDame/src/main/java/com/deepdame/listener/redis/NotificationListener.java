package com.deepdame.listener.redis;

import com.deepdame.websockets.dto.FriendRequestDto;
import com.deepdame.websockets.dto.GameNotificationDto;
import com.sefault.redis.annotation.RedisListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationListener {
    private final SimpMessagingTemplate messagingTemplate;

    @RedisListener(topic = "friend-invitation")
    public void friendRequestHandler(FriendRequestDto friendInvitation) {
        messagingTemplate.convertAndSend("/topic/user/" + friendInvitation.getReceiverId() + "/friend-request-notifications", friendInvitation);
    }

    @RedisListener(topic = "game-invitation")
    public void gameInvitationHandler(GameNotificationDto gameInvitation) {
        messagingTemplate.convertAndSend("/topic/user/" + gameInvitation.receiverId() + "/game-notifications", gameInvitation);
    }
}
