package com.deepdame.config;

import com.deepdame.listener.redis.chat.GeneralChatListener;
import com.deepdame.listener.redis.game.GameChatListener;
import com.deepdame.listener.redis.game.GameMoveListener;
import com.deepdame.listener.redis.game.GameOverListener;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisListenerConfig {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter gameMoveAdapter,
                                            MessageListenerAdapter gameChatAdapter,
                                            MessageListenerAdapter gameOverAdapter,
                                            MessageListenerAdapter generalChatAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(gameMoveAdapter, new PatternTopic("game-updates"));
        container.addMessageListener(gameChatAdapter, new PatternTopic("game-chat"));
        container.addMessageListener(gameOverAdapter, new PatternTopic("game-over"));
        container.addMessageListener(generalChatAdapter, new PatternTopic("general-chat"));
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

    @Bean
    MessageListenerAdapter generalChatAdapter(GeneralChatListener receiver) {
        return new MessageListenerAdapter(receiver, "onMessage");
    }
}
