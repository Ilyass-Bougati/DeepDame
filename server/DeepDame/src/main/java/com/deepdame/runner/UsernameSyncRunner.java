package com.deepdame.runner;

import com.deepdame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class UsernameSyncRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String KEY = "usernames:taken";

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        redisTemplate.delete(KEY);

        System.out.println("Beginning username synchronization to Redis...");
        long start = System.currentTimeMillis();
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            try (Stream<String> usernameStream = userRepository.findAllUsernames()) {
                usernameStream.forEach(username -> {
                    byte[] keyBytes = redisTemplate.getStringSerializer().serialize(KEY);
                    byte[] valueBytes = redisTemplate.getStringSerializer().serialize(username.toLowerCase());
                    connection.setCommands().sAdd(keyBytes, valueBytes);
                });
            }
            return null;
        });

        long end = System.currentTimeMillis();
        log.info("Synced {} usernames to Redis in {} ms", results.size(), (end - start));
    }
}
