package com.deepdame.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiting")
public record RateLimitingProperties(
        @NotNull Long capacity,
        @NotNull Long refillTokens
) {
}
