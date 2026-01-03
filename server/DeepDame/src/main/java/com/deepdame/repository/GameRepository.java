package com.deepdame.repository;

import com.deepdame.entity.GameDocument;
import com.deepdame.enums.GameMode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends MongoRepository<GameDocument, UUID> {

    List<GameDocument> findByModeAndPlayerWhiteIdIsNull(GameMode gameMode);

    List<GameDocument> findByPlayerBlackIdOrPlayerWhiteId(UUID playerId, UUID playerId1);

    long countByWinnerId(UUID playerId);

    @Query(value = "{ '$or': [ { 'playerBlackId' : ?0 }, { 'playerWhiteId' : ?0 } ] }", count = true)
    long countTotalGamesPlayed(UUID userId);

    @Query(value = "{ 'winnerId': ?0, 'gameDate': { $gte: ?1 } }", count = true)
    long countWinsSince(UUID userId, LocalDateTime date);

    @Query(value = "{ '$or': [ { 'playerBlackId' : ?0 }, { 'playerWhiteId' : ?0 } ], 'gameDate': { $gte: ?1 } }", count = true)
    long countGamesSince(UUID userId, LocalDateTime date);

    @Query(value = "{ '$or': [ " +
            "{ 'playerBlackId': ?0, 'playerWhiteId': ?1 }, " +
            "{ 'playerBlackId': ?1, 'playerWhiteId': ?0 } " +
            "] }", count = true)
    long countGamesBetween(UUID user1, UUID user2);

    @Query(value = "{ 'winnerId': ?0, '$or': [ " +
            "{ 'playerBlackId': ?0, 'playerWhiteId': ?1 }, " +
            "{ 'playerBlackId': ?1, 'playerWhiteId': ?0 } " +
            "] }", count = true)
    long countWinsAgainst(UUID me, UUID opponent);
}
