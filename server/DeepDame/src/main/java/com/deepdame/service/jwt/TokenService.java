package com.deepdame.service.jwt;

import com.deepdame.dto.auth.LoginRequest;
import com.deepdame.entity.User;
import com.deepdame.exception.Unauthorized;
import com.deepdame.properties.JwtProperties;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.security.CustomUserDetailsService;
import com.deepdame.service.user.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final UserEntityService userEntityService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public Token generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.accessTokenExpirationDuration() * 60 * 60, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.refreshTokenExpirationDuration() * 60 * 60, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .build();

        String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
        String refreshToken =  this.jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        userEntityService.updateRefreshToken(authentication.getName(), refreshToken);

        return Token.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
    }

    public Token refreshToken(String refreshToken) {
        User user = userEntityService.findByRefreshToken(refreshToken);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
        Token token = generateToken(authentication);

        // changing the users token
        userEntityService.updateRefreshToken(user.getEmail(), token.getRefresh_token());
        return token;
    }

    public Token login(LoginRequest loginRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        User user = userEntityService.findByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Unauthorized("Invalid Credentials");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), null, userDetails.getAuthorities());

        return generateToken(authentication);
    }
}