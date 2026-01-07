package com.deepdame.dto.game;

import com.deepdame.entity.mongo.GameDocument;
import org.springframework.stereotype.Component;

@Component
public class GameMapperImpl implements GameMapper{

    @Override
    public GameDto toDTO(GameDocument entity){
        if (entity == null) return null;

        return GameDto.builder()
                .id(entity.getId())
                .gameState(entity.getGameState())
                .mode(entity.getMode())
                .playerBlackId(entity.getPlayerBlackId())
                .playerWhiteId(entity.getPlayerWhiteId())
                .gameDate(entity.getGameDate())
                .history(entity.getHistory())
                .build();
    }

    @Override
    public GameDocument toEntity(GameDto gameDto){
        if (gameDto == null) return null;

        return GameDocument.builder()
                .id(gameDto.getId())
                .gameState(gameDto.getGameState())
                .mode(gameDto.getMode())
                .playerBlackId(gameDto.getPlayerBlackId())
                .playerWhiteId(gameDto.getPlayerWhiteId())
                .gameDate(gameDto.getGameDate())
                .history(gameDto.getHistory())
                .build();
    }
}
