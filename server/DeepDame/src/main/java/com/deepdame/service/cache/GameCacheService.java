package com.deepdame.service.cache;

import com.deepdame.entity.mongo.GameDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GameCacheService {

    private final RedisTemplate<String, GameDocument> gameTemplate;

    private final StringRedisTemplate stringTemplate;

    private static final String KEY_GAME = "game:";
    private static final String KEY_LOBBY = "lobby:open_games";
    private static final String KEY_USER_GAME = "user:%s:active_game";

    @Value("${game.cache.ttl-in-millis}")
    private long gameTtlMillis;


    // game stuff
    public void saveGame(GameDocument game){

        String key = KEY_GAME + game.getId();

        gameTemplate.opsForValue().set(key, game, gameTtlMillis, TimeUnit.MILLISECONDS);

        refreshUserGameTtl(game.getPlayerBlackId());
        refreshUserGameTtl(game.getPlayerWhiteId());
    }


    public GameDocument getGame(UUID gameId){
        return gameTemplate.opsForValue().get(KEY_GAME + gameId);
    }

    public void deleteGame(UUID gameId){

        String key = KEY_GAME + gameId;

        gameTemplate.delete(key);
    }


    // lobby stuff
    public void addToLobby(UUID gameId){
        stringTemplate.opsForSet().add(KEY_LOBBY, gameId.toString());
    }

    public void removeFromLobby(UUID gameId){
        stringTemplate.opsForSet().remove(KEY_LOBBY, gameId.toString());
    }

    public List<GameDocument> getOpenGames(){

        Set<String> idSet = stringTemplate.opsForSet().members(KEY_LOBBY);

        if (idSet == null || idSet.isEmpty()) return List.of();

        List<String> idList = new ArrayList<>(idSet);

        return fetchGamesBatch(idList);
    }

    public UUID getRandomOpenGameId() {
        String idString = stringTemplate.opsForSet().randomMember(KEY_LOBBY);

        if (idString == null) return null;
        return UUID.fromString(idString);
    }


    // user game stuff
    public void setUserCurrentGame(UUID userId, UUID gameId){
        String key = String.format(KEY_USER_GAME, userId);
        stringTemplate.opsForValue().set(key, gameId.toString(), gameTtlMillis, TimeUnit.MILLISECONDS);
    }

    public UUID getUserCurrentGameId(UUID userId){
        String key = String.format(KEY_USER_GAME, userId);
        String gameIdString = stringTemplate.opsForValue().get(key);
        return (gameIdString == null) ? null : UUID.fromString(gameIdString);
    }

    public boolean isUserPlaying(UUID userId){

        UUID gameId = getUserCurrentGameId(userId);

        Boolean gameExists = gameTemplate.hasKey(KEY_GAME + gameId);

        if (Boolean.FALSE.equals(gameExists)) {
            clearUserCurrentGame(userId);
            removeFromLobby(gameId);
            return false;
        }
        return true;
    }

    public void clearUserCurrentGame(UUID userId){
        stringTemplate.delete(String.format(KEY_USER_GAME, userId));
    }

    private void refreshUserGameTtl(UUID userId) {

        String key = String.format(KEY_USER_GAME, userId);
        stringTemplate.expire(key, gameTtlMillis, TimeUnit.MILLISECONDS);
    }


    // helper function for fetching the open games and helling the lobby set
    private List<GameDocument> fetchGamesBatch(List<String> idList){

        List<String> keys = idList.stream()
                .map(id -> KEY_GAME + id)
                .toList();

        List<GameDocument> rawGames = gameTemplate.opsForValue().multiGet(keys);

        if (rawGames == null) return List.of();

        // the game object in redis have ttl of 1h but lobby does not have a ttl
        // so we need to remove the ids of the dead games manually
        List<GameDocument> validGames = new ArrayList<>();
        List<String> staleIds = new ArrayList<>();

        for (int i = 0; i < rawGames.size(); i++){
            GameDocument game = rawGames.get(i);

            if (game == null){
                staleIds.add(idList.get(i));
            } else {
                validGames.add(game);
            }
        }

        if (!staleIds.isEmpty()){
            stringTemplate.opsForSet().remove(KEY_LOBBY, staleIds.toArray());
        }

        return validGames;
    }
}
