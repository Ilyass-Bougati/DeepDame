package com.deepdame.config;

import com.deepdame.entity.mongo.GameDocument;
import com.deepdame.listener.redis.GameChatListener;
import com.deepdame.listener.redis.GameMoveListener;
import com.deepdame.listener.redis.GameOverListener;
import com.deepdame.properties.RedisProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisListenerConfig {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter gameMoveAdapter,
                                            MessageListenerAdapter gameChatAdapter,
                                            MessageListenerAdapter gameOverAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(gameMoveAdapter, new PatternTopic("game-updates"));
        container.addMessageListener(gameChatAdapter, new PatternTopic("game-chat"));
        container.addMessageListener(gameOverAdapter, new PatternTopic("game-over"));
        return container;
    }

    @Bean
    MessageListenerAdapter gameMoveAdapter(GameMoveListener receiver) {
        return new MessageListenerAdapter(receiver, "onMessage");
    }

    @Bean
    MessageListenerAdapter gameChatAdapter(GameChatListener receiver) {
        return new MessageListenerAdapter(receiver, "onMessage");
    }

    @Bean
    MessageListenerAdapter gameOverAdapter(GameOverListener receiver) {
        return new MessageListenerAdapter(receiver, "onMessage");
    }
}
