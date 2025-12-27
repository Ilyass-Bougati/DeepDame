package com.deepdame.service.jwt;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String access_token;
    private String refresh_token;

    private Instant expires_at;
}