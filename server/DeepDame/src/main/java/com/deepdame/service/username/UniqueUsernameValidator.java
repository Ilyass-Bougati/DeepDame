package com.deepdame.service.username;

import jakarta.validation.ConstraintValidator;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY = "usernames:taken";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isBlank()) {
            return true;
        }

        String normalized = username.trim().toLowerCase();
        Boolean isTaken = redisTemplate.opsForSet().isMember(KEY, normalized);
        return !Boolean.TRUE.equals(isTaken);
    }
}
