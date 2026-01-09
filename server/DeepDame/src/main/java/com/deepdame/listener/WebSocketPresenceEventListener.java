package com.deepdame.listener;

import com.deepdame.entity.mongo.GameDocument;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.cache.GameCacheService;
import com.deepdame.service.game.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketPresenceEventListener {

    private final GameCacheService gameCacheService;
    private final GameService gameService;

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

            UUID userId = user.getUser().getId();
            stringRedisTemplate.opsForSet().remove(KEY_ONLINE_USERS, userId.toString());
            log.trace("User offline: {}", user.getUsername());

            handleAutoSurrender(userId);
        }
    }

    private void handleAutoSurrender(UUID userId) {
        try{
            UUID activeGameId = gameCacheService.getUserCurrentGameId(userId);

            if(activeGameId != null){
                GameDocument game = gameCacheService.getGame(activeGameId);

                if (game.getPlayerWhiteId() != null){
                    gameService.surrenderGame(activeGameId, userId);
                } else {
                    gameCacheService.deleteGame(activeGameId);
                    gameCacheService.removeFromLobby(activeGameId);
                    gameCacheService.clearUserCurrentGame(userId);
                }
            }
        } catch (Exception e) {
            log.trace("Auto-surrender skipped for sender {}: {}", userId, e.getMessage());
        }
    }
}