package com.deepdame.listeners;

import com.deepdame.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketPresenceEventListener {

    private final StringRedisTemplate stringRedisTemplate;
    public static final String KEY_ONLINE_USERS = "stats:online_users";

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (sha.getUser() instanceof UsernamePasswordAuthenticationToken auth
                && auth.getPrincipal() instanceof CustomUserDetails user) {

            String userId = user.getUser().getId().toString();
            stringRedisTemplate.opsForSet().add(KEY_ONLINE_USERS, userId);
            log.trace("User online: {}", user.getUsername());
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (sha.getUser() instanceof UsernamePasswordAuthenticationToken auth
                && auth.getPrincipal() instanceof CustomUserDetails user) {

            String userId = user.getUser().getId().toString();
            stringRedisTemplate.opsForSet().remove(KEY_ONLINE_USERS, userId);
            log.trace("User offline: {}", user.getUsername());
        }
    }
}