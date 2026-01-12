package com.deepdame.service.game;

import com.deepdame.dto.game.GameDto;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.GameMode;
import com.deepdame.service.CrudDtoService;

import java.util.List;
import java.util.UUID;

public interface GameService extends CrudDtoService<UUID, GameDto> {

    GameDto createGame(UUID playerId, GameMode mode);
    GameDto joinGame(UUID gameId, UUID playerId);
    GameDto makeMove(UUID gameId, UUID playerId, Move move);

    GameDto makeAiMove(UUID gameId);

    GameDto surrenderGame(UUID gameId, UUID playerId);

    List<GameDto> getOpenGames();
    List<GameDto> getUserFinishedGames(UUID playerId);
    GameDto getUserCurrentGame(UUID playerId);

    GameDto findOrStartMatch(UUID playerId);
}
