package com.deepdame.controller;

import com.deepdame.dto.user.UserDto;
import com.deepdame.dto.user.UserMapper;
import com.deepdame.exception.Unauthorized;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;

    @GetMapping("/")
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            throw new Unauthorized("User not authenticated");
        } else {
            return ResponseEntity.ok(userMapper.toDTO(principal.getUser()));
        }
    }
}
