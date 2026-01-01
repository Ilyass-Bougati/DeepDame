package com.deepdame.service.cache;

import com.deepdame.entity.GameDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GameCacheService {

    private final RedisTemplate<String, GameDocument> gameTemplate;

    private final StringRedisTemplate stringTemplate;

    private static final String KEY_GAME = "game:";
    private static final String KEY_LOBBY = "lobby:open_games";

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

    public void addToLobby(UUID gameId){
        stringTemplate.opsForSet().add(KEY_LOBBY, gameId.toString());
    }

    public void removeFromLobby(UUID gameId){
        stringTemplate.opsForSet().remove(KEY_LOBBY, gameId.toString());
    }

    public List<GameDocument> getOpenGames(){

        Set<String> ids = stringTemplate.opsForSet().members(KEY_LOBBY);

        List<GameDocument> gameDocuments = new ArrayList<>();

        for (String id : ids){
            gameDocuments.add(getGame(UUID.fromString(id)));
        }
        return gameDocuments;
    }
}
