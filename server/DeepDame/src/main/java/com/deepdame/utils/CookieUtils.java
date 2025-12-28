package com.deepdame.utils;

import org.springframework.http.ResponseCookie;

public class CookieUtils {

    public static ResponseCookie genCookie(String key, String value, long maxAge, String path) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(false)
                .path(path)
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }
}