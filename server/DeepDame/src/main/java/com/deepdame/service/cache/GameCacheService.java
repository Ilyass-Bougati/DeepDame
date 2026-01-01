package com.deepdame.service.cache;

import com.deepdame.entity.GameDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GameCacheService {

    private final RedisTemplate<String, GameDocument> gameTemplate;

    private static final String KEY_GAME = "game:";

    public void saveGame(GameDocument game){

        String key = KEY_GAME + game.getId();

        gameTemplate.opsForValue().set(key, game, 1, TimeUnit.HOURS);
    }


    public GameDocument getGame(UUID gameId){
        return gameTemplate.opsForValue().get(KEY_GAME + gameId);
    }

    public void deleteGame(UUID gameId){

        String key = KEY_GAME + gameId;

        gameTemplate.delete(key);
    }
}
