package com.deepdame;

import com.deepdame.properties.DefaultAdminRunnerProperties;
import com.deepdame.properties.JwtProperties;
import com.deepdame.properties.RateLimitingProperties;
import com.deepdame.properties.RedisProperties;
import com.deepdame.properties.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({
        JwtProperties.class,
        RedisProperties.class,
        RateLimitingProperties.class,
        DefaultAdminRunnerProperties.class,
        EmailSenderProperties.class
})
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
