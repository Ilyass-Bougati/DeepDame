package com.deepdame.config;

import com.deepdame.engine.core.logic.GameEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Bean
    public GameEngine gameEngine() {
        return new GameEngine();
    }
}
