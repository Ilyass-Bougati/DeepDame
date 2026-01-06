package com.deepdame.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "default-admin")
public record DefaultAdminRunnerProperties(
        String username,
        String email,
        String password
) {
}
