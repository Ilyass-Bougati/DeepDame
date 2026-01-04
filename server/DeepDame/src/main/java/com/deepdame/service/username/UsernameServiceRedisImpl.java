package com.deepdame.service.username;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsernameServiceRedisImpl implements UsernameService {
    private final StringRedisTemplate redisTemplate;
    private static final String KEY = "usernames:taken";

    public Boolean reserveUsername(String username) {
        String normalized = username.trim().toLowerCase();
        Long result = redisTemplate.opsForSet().add(KEY, normalized);
        return result != null && result > 0;
    }

    public Boolean isTaken(String username) {
        String normalized = username.trim().toLowerCase();
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(KEY, normalized));
    }

    public void releaseUsername(String username) {
        String normalized = username.trim().toLowerCase();
        redisTemplate.opsForSet().remove(KEY, normalized);
    }
}
