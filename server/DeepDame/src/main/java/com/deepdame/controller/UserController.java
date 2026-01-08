package com.deepdame.controller;

import com.deepdame.dto.auth.ChangePasswordRequest;
import com.deepdame.dto.user.UserDto;
import com.deepdame.dto.user.UserMapper;
import com.deepdame.exception.Unauthorized;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.jwt.TokenService;
import com.deepdame.service.user.PasswordForgottenService;
import com.deepdame.service.user.UserService;
import com.deepdame.service.username.UniqueUsername;
import com.deepdame.utils.CookieUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordForgottenService passwordForgottenService;
    private final TokenService tokenService;
    private final JwtDecoder jwtDecoder;

    @GetMapping("/")
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(principal.getUser());
    }

    @PostMapping("/changepw")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(principal.getUser().getId(), request.oldPassword(), request.newPassword());
    }

    @PostMapping("/change-username")
    @ResponseStatus(HttpStatus.OK)
    public void changeUsername(@AuthenticationPrincipal CustomUserDetails principal, @RequestParam String newUsername) {
        userService.changeUsername(principal.getUser().getId(), newUsername);
    }

    @PostMapping("/forgotPassword/")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        String token = passwordForgottenService.passwordForgotten(email.toLowerCase());

        ResponseCookie cookie = ResponseCookie.from("validation_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(10 * 60)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Validation code sent to email"));
    }

    @PostMapping("/forgotPassword/validateEmail")
    public ResponseEntity<?> validateEmail(@CookieValue(name = "validation_token", required = true) String validationToken, @RequestParam Integer validationCode) {
        Boolean emailValidated = passwordForgottenService.validateEmail(validationToken, validationCode);
        if (!emailValidated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String pwChangeToken = tokenService.passwordChangeToken(validationToken);
        ResponseCookie cookie = ResponseCookie.from("pwchange_token", pwChangeToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3 * 60)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Email validated successfully"));
    }

    @PostMapping("/forgotPassword/changePassword")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> validateEmail(@CookieValue(name = "pwchange_token", required = true) String passwordChangeToken, @RequestParam String newPassword) {
        Jwt jwt = jwtDecoder.decode(passwordChangeToken);

        if (!jwt.getClaim("scope").toString().equals("pwchange")) {
            throw new Unauthorized("Unauthorized to change password");
        }

        String email = jwt.getClaim("sub").toString();
        userService.changePassword(email, newPassword);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("pwchange_token", "",  0, "/").toString())
                .header(HttpHeaders.SET_COOKIE, CookieUtils.genCookie("validation_token", "",  0, "/").toString())
                .body(Map.of("message", "Password changed successfully"));
    }
}
