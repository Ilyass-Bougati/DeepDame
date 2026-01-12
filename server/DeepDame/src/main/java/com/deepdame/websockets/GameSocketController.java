package com.deepdame.websockets;

import com.deepdame.dto.game.GameDto;
import com.deepdame.dto.redis.GameChatMessage;
import com.deepdame.dto.redis.GameMoveMessageDto;
import com.deepdame.dto.redis.GameOverMessage;
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
import java.util.concurrent.CompletableFuture;

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
                    hostUser.getEmail(),
                    "/queue/game/joined",
                    new GameJoinedResponse(game.getId(), me.getUsername(), "BLACK")
            );
        }
    }

    @MessageMapping("/game/{gameId}/move")
    public void makeMove(@AuthenticationPrincipal CustomUserDetails user, @DestinationVariable UUID gameId, @Payload Move move) {

        UUID playerId = user.getUser().getId();

        log.trace(" [MOVE_REQ] Game: {} | Player: {} ({}) | Move: {} -> {}",
                gameId, user.getUser().getUsername(), playerId, move.from(), move.to());

        GameDto game = gameService.makeMove(gameId, playerId, move);

        log.trace(" [MOVE_OK] Game: {} | Player: {} | Result: Move processed successfully",
                gameId, user.getUser().getUsername());

        redisNotificationService.sendMessage(GameMoveMessageDto.builder().gameId(gameId).move(move).build(), "game-updates");

        if (game.getGameState().isGameOver()){
            log.trace(" [GAME_OVER] Game: {} | Winner found after move by {}", gameId, user.getUser().getUsername());
            notifyGameOver(game);
        }

        if (game.getMode() == GameMode.PVE && !game.getGameState().isGameOver()){
            CompletableFuture.runAsync(() -> {
                try {
                    GameDto updatedGame = gameService.makeAiMove(gameId);

                    if (updatedGame != null) {
                        List<Move> history = updatedGame.getHistory();
                        Move aiMove = history.get(history.size() - 1);

                        redisNotificationService.sendMessage(
                                GameMoveMessageDto.builder().gameId(gameId).move(aiMove).build(),
                                "game-updates"
                        );

                        if (updatedGame.getGameState().isGameOver()) {
                            notifyGameOver(updatedGame);
                        }
                    }
                } catch (Exception e) {
                    log.trace("Error in AI move", e);
                }
            });
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

        GameChatMessage response = new GameChatMessage(
                username,
                request.content(),
                Instant.now().toString(),
                gameId
        );

        redisNotificationService.sendMessage(response, "game-chat");

    }

    private void broadcastAiMove(GameDto game, Move userMove){

        List<Move> history = game.getHistory();

        if (history != null && !history.isEmpty()){
            Move lastMove = history.get(history.size() - 1);

            boolean isSameMove = lastMove.from().equals(userMove.from()) && lastMove.to().equals(userMove.to());

            if (!isSameMove){
                log.debug("Broadcasting AI Move for Game {}", game.getId());
                redisNotificationService.sendMessage(GameMoveMessageDto.builder().gameId(game.getId()).move(lastMove).build(), "game-updates");
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
        GameOverMessage response = new GameOverMessage(winnerColor, winnerName, winnerId, gameDto.getId());
        redisNotificationService.sendMessage(response, "game-over");
    }

    private void sendErrorMessage(String username, String type, String message) {
        ErrorDto error = new ErrorDto(type, message);
        messagingTemplate.convertAndSendToUser(username, "/queue/errors", error);
    }

    public record ErrorDto(String type, String message) {}
    public record GameCreatedResponse(UUID gameId) {}
    public record GameJoinedResponse(UUID gameId, String opponentName, String yourColor) {}
    public record ChatRequest(String content) {}
}
