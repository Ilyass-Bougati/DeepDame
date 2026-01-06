package com.deepdame.config;

import com.deepdame.properties.RateLimitingProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RateLimitingConfig {
    private final RateLimitingProperties rateLimitingProperties;

    @Bean
    public Bucket bucket() {
        // Define the bandwidth with a limit of 10 tokens, refilled every minute
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitingProperties.capacity())
                .refillGreedy(rateLimitingProperties.refillTokens(), Duration.ofMinutes(1))
                .build();

        return Bucket.builder().addLimit(limit).build();
    }
}
