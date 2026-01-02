package com.deepdame.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "deepdame.cache.redis")
public record RedisProperties (Long timeToLive) {
}
