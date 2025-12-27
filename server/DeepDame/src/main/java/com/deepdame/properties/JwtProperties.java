package com.deepdame.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        RSAPublicKey accessTokenPublicKey,
        RSAPrivateKey accessTokenPrivateKey,
        RSAPublicKey refreshTokenPublicKey,
        RSAPrivateKey refreshTokenPrivateKey
) {}