package com.deepdame;

import com.deepdame.entity.User;
import com.deepdame.properties.DefaultAdminRunnerProperties;
import com.deepdame.properties.JwtProperties;
import com.deepdame.properties.RateLimitingProperties;
import com.deepdame.properties.RedisProperties;
import com.deepdame.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableCaching
@EnableConfigurationProperties({
        JwtProperties.class,
        RedisProperties.class,
        RateLimitingProperties.class,
        DefaultAdminRunnerProperties.class
})
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Create Admin
            saveUser(userRepository, passwordEncoder, "super.admin", "admin@gmail.com", "admin", "ADMIN");

            // 2. Create Players
            saveUser(userRepository, passwordEncoder, "player10", "player1@gmail.com", "password", "USER");
            saveUser(userRepository, passwordEncoder, "player11", "player2@gmail.com", "password", "USER");
            saveUser(userRepository, passwordEncoder, "player12", "player3@gmail.com", "password", "USER");
            saveUser(userRepository, passwordEncoder, "player13", "player4@gmail.com", "password", "USER");
            saveUser(userRepository, passwordEncoder, "player14", "player5@gmail.com", "password", "USER");
        };
    }

    private void saveUser(UserRepository repository, PasswordEncoder encoder, String username, String email, String password, String role) {

        if (repository.findByEmail(email).isEmpty()) {
            var user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(encoder.encode(password));

            repository.save(user);
            System.out.println(" > Created " + role + ": " + username);
        }
    }

}
