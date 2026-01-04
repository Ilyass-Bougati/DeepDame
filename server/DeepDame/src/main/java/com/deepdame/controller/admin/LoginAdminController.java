package com.deepdame.controller.admin;

import com.deepdame.security.AdminAuthenticationManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/login")
    public String login() {
        return "admin/auth/login";
    }

    @PostMapping("/perform_login")
    public String performLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpServletRequest request) {
        try {
            log.debug("Attempting to perform login");
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return "redirect:/admin";

        } catch (AuthenticationException e) {
            log.debug("Authentication failed");
            return "redirect:/admin/login?error=true";
        }
    }
}


