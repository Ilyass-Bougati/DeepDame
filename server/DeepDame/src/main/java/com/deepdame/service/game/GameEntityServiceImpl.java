package com.deepdame.service.game;

import com.deepdame.entity.mongo.GameDocument;
import com.deepdame.enums.GameMode;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.mongo.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class GameEntityServiceImpl implements GameEntityService{

    final private GameRepository gameRepository;

    @Override
    @Transactional(readOnly = true)
    public GameDocument findById(UUID uuid) {
        return gameRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException("Game not found with ID: + id"));
    }

    @Override
    public List<GameDocument> findAll() {
        return gameRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDocument> findOpenPvpGames() {
        return gameRepository.findByModeAndPlayerWhiteIdIsNull(GameMode.PVP);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDocument> findGamesByPlayerId(UUID playerId) {
        return gameRepository.findByPlayerBlackIdOrPlayerWhiteId(playerId, playerId);
    }
}
