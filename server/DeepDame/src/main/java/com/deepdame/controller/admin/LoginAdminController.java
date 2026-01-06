package com.deepdame.controller.admin;

import com.deepdame.dto.auth.LoginRequest;
import com.deepdame.exception.Unauthorized;
import com.deepdame.security.AdminAuthenticationManager;
import com.deepdame.service.jwt.Token;
import com.deepdame.service.jwt.TokenService;
import com.deepdame.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class LoginAdminController {
    private final AdminAuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @GetMapping("/login")
    public String login() {
        return "admin/auth/login";
    }

    @PostMapping("/perform_login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpServletResponse response) {
        try {
            log.debug("Admin authentication attempt: {}", email);

            LoginRequest loginRequest = new LoginRequest(email, password);
            Token token = tokenService.login(loginRequest);

            ResponseCookie accessCookie = CookieUtils.genCookie(
                    "access_token",
                    token.getAccess_token(),
                    7200,
                    "/"
            );

            ResponseCookie refreshCookie = CookieUtils.genCookie(
                    "refresh_token",
                    token.getRefresh_token(),
                    86400,
                    "/api/auth/refresh"
            );

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            log.info("Admin {} authenticated successfully", email);
            return "redirect:/admin";

        } catch (Exception e) {
            log.warn("Login failed for {}: {}", email, e.getMessage());
            return "redirect:/admin/login?error=true";
        }
    }
}


