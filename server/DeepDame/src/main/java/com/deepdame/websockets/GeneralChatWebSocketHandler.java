package com.deepdame.websockets;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.service.json.JsonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeneralChatWebSocketHandler extends TextWebSocketHandler {
    // Keeping track of sessions manually
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final JsonService<GeneralChatMessageDto> jsonService;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        GeneralChatMessageDto msg =  jsonService.parseJson(payload, GeneralChatMessageDto.class);
        log.info("Received from {}: {}", session.getId(), msg.getMessage());

        // Here we send a response to the other player
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage("Echo: " + payload));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("Disconnected from web socket at {}", session.getId());
    }
}
