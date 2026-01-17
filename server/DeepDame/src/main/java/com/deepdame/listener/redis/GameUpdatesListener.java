package com.deepdame.listener.redis;

import com.deepdame.dto.redis.GameChatMessage;
import com.deepdame.dto.redis.GameMoveMessageDto;
import com.deepdame.dto.redis.GameOverMessage;
import com.sefault.redis.annotation.RedisListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameUpdatesListener {
    private final SimpMessagingTemplate messagingTemplate;

    @RedisListener(topic = "game-chat")
    public void gameChatMessageHandler(GameChatMessage message) {
        messagingTemplate.convertAndSend("/topic/game/" + message.gameId() + "/chat", message);
    }

    @RedisListener(topic = "game-updates")
    public void gameMoveHandler(GameMoveMessageDto move) {
        messagingTemplate.convertAndSend("/topic/game/" + move.getGameId(), move.getMove());
    }

    @RedisListener(topic = "game-over")
    public void gameOverHandler(GameOverMessage message) {
        messagingTemplate.convertAndSend("/topic/game/" + message.gameId() + "/game-over", message);
    }
}
