package com.deepdame.config;

import com.deepdame.entity.GameDocument;
import com.deepdame.websockets.GameWebSocketHandler;
import com.deepdame.websockets.GeneralChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {
    private final GameWebSocketHandler gameWebSocketHandler;
    private final GeneralChatWebSocketHandler generalChatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler, "/game-ws/v1")
                .setAllowedOrigins("*");
        registry.addHandler(generalChatWebSocketHandler, "/general-chat-ws/v1")
                .setAllowedOrigins("*");
    }
}
