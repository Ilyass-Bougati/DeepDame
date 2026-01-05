package com.deepdame.controller;

import com.deepdame.dto.auth.LoginRequest;
import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.jwt.Token;
import com.deepdame.service.jwt.TokenService;
import com.deepdame.service.user.UserService;
import com.deepdame.service.username.UsernameService;
import com.deepdame.utils.CookieUtils;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final UserService userService;
    private final UsernameService usernameService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        Token token = tokenService.login(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("access_token", token.getAccess_token(), 7200, "/").toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("refresh_token", token.getRefresh_token(), 86400, "/api/auth/refresh").toString())
                .body(Map.of("message", "Logged in successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails principal) {
        userService.logout(principal.getUser().getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("access_token", "", 0, "/").toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("refresh_token", "", 0, "/api/auth/refresh").toString())
                .body(Map.of("message", "Logged out in successfully"));
    }

    @GetMapping("/checkUsername/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable @NonNull String username) {
        Boolean isTaken = usernameService.isTaken(username.toLowerCase());
        return ResponseEntity.ok(Map.of("message", isTaken));
    }
}