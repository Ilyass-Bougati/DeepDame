package com.deepdame.repository;

import com.deepdame.entity.GameDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends MongoRepository<GameDocument, UUID> {
}
