package com.deepdame.runner;

import com.deepdame.controller.AuthController;
import com.deepdame.dto.auth.LoginRequest;
import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.entity.Role;
import com.deepdame.entity.User;
import com.deepdame.properties.DefaultAdminRunnerProperties;
import com.deepdame.repository.RoleRepository;
import com.deepdame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Order(3)
@Transactional
@RequiredArgsConstructor
public class DefaultAdminRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthController authController;
    private final DefaultAdminRunnerProperties defaultAdminRunnerProperties;

    @Override
    public void run(String... args) throws Exception {
        String defaultAdminUsername = defaultAdminRunnerProperties.username();
        String defaultAdminEmail    = defaultAdminRunnerProperties.email();
        String defaultAdminPassword = defaultAdminRunnerProperties.password();

        // checking if the default admin already exists
        if (userRepository.existsByEmail(defaultAdminEmail)) {
            log.info("Default admin exists");
            return;
        } else {
            log.info("Default admin does not exist");
        }

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(defaultAdminUsername)
                .email(defaultAdminEmail)
                .password(defaultAdminPassword)
                .build();

        authController.register(registerRequest);

        // making the sender admin
        User user =  userRepository.findByEmail(defaultAdminEmail)
                .orElseThrow(() -> new RuntimeException("Error creating default admin"));
        Role role = roleRepository.findByName("SUPER-ADMIN")
                .orElseThrow(() -> new RuntimeException("SUPER-ADMIN role doesn't exist"));

        user.getRoles().add(role);
        userRepository.save(user);
        log.info("Default admin created");
    }
}
