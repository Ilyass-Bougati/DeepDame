package com.deepdame.service.game;

import com.deepdame.entity.GameDocument;
import com.deepdame.service.CrudEntityService;

import java.util.List;
import java.util.UUID;

public interface GameEntityService extends CrudEntityService<GameDocument, UUID> {
    List<GameDocument> findOpenPvpGames();
    List<GameDocument> findGamesByPlayerId(UUID playerId);
}
