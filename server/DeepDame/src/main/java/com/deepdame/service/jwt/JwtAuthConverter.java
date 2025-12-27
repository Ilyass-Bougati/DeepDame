package com.deepdame.service.jwt;

import com.deepdame.entity.User;
import com.deepdame.exception.Unauthorized;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.user.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserEntityService userEntityService;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        User user = userEntityService.findByEmail(jwt.getSubject());

        if (user == null || user.getBannedFromApp()) {
            throw new Unauthorized("Account doesn't exist or is banned");
        }
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "N/A",
                userDetails.getAuthorities()
        );
    }
}