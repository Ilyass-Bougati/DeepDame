package com.deepdame.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smtp")
public record EmailSenderProperties(String sendingEmail) {
}
