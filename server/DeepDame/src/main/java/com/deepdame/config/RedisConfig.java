package com.deepdame.config;

import com.deepdame.entity.GameDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, GameDocument> gameRedisTemplate(RedisConnectionFactory connectionFactory){

        RedisTemplate<String, GameDocument> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        JacksonJsonRedisSerializer<GameDocument> serializer = new JacksonJsonRedisSerializer<>(GameDocument.class);
        template.setValueSerializer(serializer);

        return template;
    }
}
