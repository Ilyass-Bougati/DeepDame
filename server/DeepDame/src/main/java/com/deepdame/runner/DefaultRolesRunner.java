package com.deepdame.runner;

import com.deepdame.entity.Role;
import com.deepdame.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DefaultRolesRunner implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        ArrayList<String> defaultRoles = new ArrayList<>(List.of("USER", "ADMIN", "SUPER_ADMIN"));
        boolean defaultRolesExists = true;

        for (String roleName : defaultRoles) {
            defaultRolesExists = defaultRolesExists && roleRepository.existsByName(roleName);
        }

        if (defaultRolesExists) {
            log.info("Default roles are already exist");
        } else {
            log.info("Initializing default roles");

            // Deleting all roles, in case some of the roles are initialized and others aren't
            roleRepository.deleteAll();
            for (String roleName : defaultRoles) {
                roleRepository.save(Role.builder().name(roleName).build());
            }

            log.info("Initialized default roles");
        }
    }
}
