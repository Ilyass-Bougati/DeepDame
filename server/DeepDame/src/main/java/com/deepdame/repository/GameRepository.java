package com.deepdame.repository;

import com.deepdame.entity.GameDocument;
import com.deepdame.enums.GameMode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends MongoRepository<GameDocument, UUID> {

    List<GameDocument> findByModeAndPlayerWhiteIdIsNull(GameMode gameMode);

    List<GameDocument> findByPlayerBlackIdOrPlayerWhiteId(UUID playerId, UUID playerId1);
}
