package com.deepdame.repository.mongo;

import com.deepdame.entity.mongo.PlayerStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerStatisticsRepository extends MongoRepository<PlayerStatistics, String> {
    Optional<PlayerStatistics> findByUserId(UUID userId);
}