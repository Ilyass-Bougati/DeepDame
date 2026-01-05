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

    @MessageMapping("/game/matchmaking")
    public void findMatch(@AuthenticationPrincipal CustomUserDetails user){

        String username = user.getUsername();
        UUID playerId = user.getUser().getId();

        log.trace("User {} requested matchmaking", username);

        try{
            GameDto game = gameService.findOrStartMatch(playerId);

            if (game.getPlayerBlackId().equals(playerId) && game.getPlayerWhiteId() == null){
                messagingTemplate.convertAndSendToUser(
                        username,
                        "/queue/game/created",
                        new GameCreatedResponse(game.getId())
                );
            } else {
                UserDto hostUser = userService.findById(game.getPlayerBlackId());

                messagingTemplate.convertAndSendToUser(
                        username,
                        "/queue/game/joined",
                        new GameJoinedResponse(game.getId(), hostUser.getUsername(), "WHITE")
                );

                UserDto me = userService.findById(playerId);
                messagingTemplate.convertAndSendToUser(
                        hostUser.getUsername(),
                        "/queue/game/joined",
                        new GameJoinedResponse(game.getId(), me.getUsername(), "BLACK")
                );
            }
        } catch (Exception e) {
            sendErrorMessage(username, "MATCHMAKING_ERROR", e.getMessage());
        }
    }

    @MessageMapping("/game/{gameId}/move")
    public void makeMove(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId, @Payload Move move) {
        try {
            UUID playerId = user.getUser().getId();

            GameDto game = gameService.makeMove(gameId, playerId, move);

            messagingTemplate.convertAndSend("/topic/game/" + gameId, move);

            if (game.getGameState().isGameOver()){
                notifyGameOver(game);
            }

        } catch (Exception e) {
            sendErrorMessage(user.getUsername(), "MOVE_ERROR", e.getMessage());
        }
    }

    @MessageMapping("/game/{gameId}/surrender")
    public void surrenderGame(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId){

        try{
            UUID playerId = user.getUser().getId();
            GameDto game = gameService.surrenderGame(gameId, playerId);

            notifyGameOver(game);
        } catch (Exception e) {
            sendErrorMessage(user.getUsername(), "SURRENDER_ERROR", e.getMessage());
        }
    }

    private void notifyGameOver(GameDto gameDto){

        UUID winnerId = gameDto.getWinnerId();

        String winnerColor = (gameDto.getGameState().getWinner() != null)? gameDto.getGameState().getWinner().name() :"Unknown";
        String winnerName = "Unknown";

        if (winnerId != null){
            try{
                winnerName = userService.findById(winnerId).getUsername();
            } catch (Exception e){
                winnerName = "Unknown";
            }
        }
        GameOverResponse response = new GameOverResponse(winnerColor, winnerName, winnerId);
        messagingTemplate.convertAndSend("/topic/game/" + gameDto.getId() + "/game-over", response);
    }

    private void sendErrorMessage(String username, String type, String message) {
        ErrorDto error = new ErrorDto(type, message);
        messagingTemplate.convertAndSendToUser(username, "/queue/errors", error);
    }

    public record ErrorDto(String type, String message) {}
    public record GameCreatedResponse(UUID gameId) {}
    public record GameJoinedResponse(UUID gameId, String opponentName, String yourColor) {}
    public record GameOverResponse(String winnerColor, String winnerName, UUID winnerId) {}
}
