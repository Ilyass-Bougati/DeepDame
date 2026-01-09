package com.deepdame.websockets;

import com.deepdame.dto.game.GameDto;
import com.deepdame.dto.game.GameMoveMessageDto;
import com.deepdame.dto.user.UserDto;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.GameMode;
import com.deepdame.exception.WsUnauthorized;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.cache.RedisNotificationService;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameSocketController {

    private final GameService gameService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisNotificationService redisNotificationService;

    @MessageMapping("/game/create")
    public void createGame(@AuthenticationPrincipal CustomUserDetails user, @Payload GameMode gameMode){

        String username = user.getUsername();
        UUID playerId = user.getUser().getId();

        GameDto game = gameService.createGame(playerId, gameMode);

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/game/created",
                new GameCreatedResponse(game.getId())
        );
    }

    @MessageMapping("/game/{gameId}/join")
    public void joinGame(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId) {
        String joinerUsername = user.getUsername();
        UUID joinerId = user.getUser().getId();

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
    }

    @MessageMapping("/game/matchmaking")
    public void findMatch(@AuthenticationPrincipal CustomUserDetails user){

        String username = user.getUsername();
        UUID playerId = user.getUser().getId();

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
    }

    @MessageMapping("/game/{gameId}/move")
    public void makeMove(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId, @Payload Move move) {

        UUID playerId = user.getUser().getId();

        GameDto game = gameService.makeMove(gameId, playerId, move);

//        messagingTemplate.convertAndSend("/topic/game/" + gameId, move);
        redisNotificationService.sendMessage(GameMoveMessageDto.builder().gameId(gameId).move(move).build(), "game-updates");

        if (game.getMode() == GameMode.PVE){
            broadcastAiMove(game, move);
        }

        if (game.getGameState().isGameOver()){
            notifyGameOver(game);
        }
    }

    @MessageMapping("/game/{gameId}/surrender")
    public void surrenderGame(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId){

        UUID playerId = user.getUser().getId();
        GameDto game = gameService.surrenderGame(gameId, playerId);

        notifyGameOver(game);
    }

    @MessageMapping("/game/{gameId}/chat")
    public void sendGameMessage(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId, @Payload ChatRequest request) {

        String username = user.getUsername();
        UUID userId = user.getUser().getId();

        GameDto game = gameService.findById(gameId);

        boolean isBlack = userId.equals(game.getPlayerBlackId());
        boolean isWhite = userId.equals(game.getPlayerWhiteId());

        if (!isBlack && !isWhite) {
            throw new WsUnauthorized("You are not a participant in this game.");
        }

        ChatMessageResponse response = new ChatMessageResponse(
                username,
                request.content(),
                Instant.now().toString()
        );

        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/chat", response);

    }

    private void broadcastAiMove(GameDto game, Move userMove){

        List<Move> history = game.getHistory();

        if (history != null && !history.isEmpty()){
            Move lastMove = history.get(history.size() - 1);

            boolean isSameMove = lastMove.from().equals(userMove.from()) && lastMove.to().equals(userMove.to());

            if (!isSameMove){
                log.debug("Broadcasting AI Move for Game {}", game.getId());
                messagingTemplate.convertAndSend("/topic/game/" + game.getId(), lastMove);
            }
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
    public record ChatRequest(String content) {}
    public record ChatMessageResponse(String sender, String content, String timestamp) {}
}
