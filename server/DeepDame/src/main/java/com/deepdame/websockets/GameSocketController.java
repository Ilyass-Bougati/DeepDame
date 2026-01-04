package com.deepdame.websockets;

import com.deepdame.dto.game.GameDto;
import com.deepdame.dto.user.UserDto;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.GameMode;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.game.GameService;
import com.deepdame.service.user.UserService;
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
public class GameSocketController {

    private final GameService gameService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/create")
    public void createGame(@AuthenticationPrincipal CustomUserDetails user, @Payload GameMode gameMode){

        String username = user.getUsername();
        UUID playerId = user.getUser().getId();
        log.info("Socket request: User {} wants to create {} game", username, gameMode);

        try{
            GameDto game = gameService.createGame(playerId, gameMode);

            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/game/created",
                    new GameCreatedResponse(game.getId())
            );

            log.info("Game {} created for user {}", game.getId(), username);
        } catch (Exception e) {
            log.error("Create Game Error: {}", e.getMessage());
            sendErrorMessage(username, "CREATE_ERROR", e.getMessage());
        }
    }

    @MessageMapping("/game/{gameId}/join")
    public void joinGame(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId) {
        String joinerUsername = user.getUsername();
        UUID joinerId = user.getUser().getId();

        try {
            GameDto game = gameService.joinGame(gameId, joinerId);

            UserDto hostUser = userService.findById(game.getPlayerBlackId());
            String hostUsername = hostUser.getUsername();

            messagingTemplate.convertAndSendToUser(
                    joinerUsername,
                    "/queue/game/joined",
                    new GameJoinedResponse(gameId, hostUsername, "WHITE")
            );

            messagingTemplate.convertAndSendToUser(
                    hostUsername,
                    "/queue/game/joined",
                    new GameJoinedResponse(gameId, joinerUsername, "BLACK")
            );

            log.info("User {} joined game {}", joinerUsername, gameId);
        } catch (Exception e) {
            sendErrorMessage(user.getUsername(), "JOIN_ERROR", e.getMessage());
        }
    }

    @MessageMapping("/game/{gameId}/move")
    public void makeMove(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId, @Payload Move move) {
        try {
            UUID playerId = user.getUser().getId();

            gameService.makeMove(gameId, playerId, move);

            messagingTemplate.convertAndSend("/topic/game/" + gameId, move);

        } catch (Exception e) {
            sendErrorMessage(user.getUsername(), "MOVE_ERROR", e.getMessage());
        }
    }

    private void sendErrorMessage(String username, String type, String message) {
        ErrorDto error = new ErrorDto(type, message);
        messagingTemplate.convertAndSendToUser(username, "/queue/errors", error);
    }

    public record ErrorDto(String type, String message) {}
    public record GameCreatedResponse(UUID gameId) {}
    public record GameJoinedResponse(UUID gameId, String opponentName, String yourColor) {}
}
