package com.deepdame.service.user;

import com.deepdame.service.email.EmailService;
import com.deepdame.service.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordForgottenService {
    private final UserEntityService userEntityService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final JwtDecoder jwtDecoder;

    private static final Map<Integer, String> tokenMap = new HashMap<>();

    public String passwordForgotten(String email) {
        userEntityService.findByEmail(email);

        // generating the token
        String emailValidationToken = tokenService.getEmailValidationToken(email);

        // generating a random number
        Random rand = new Random();
        Integer validationCode = 100000 + rand.nextInt(900000); // 0-899999 + 100000
        tokenMap.put(validationCode, emailValidationToken);

        // sending the email
        emailService.passwordForgottenEmail(email, validationCode);

        return emailValidationToken;
    }

    public Boolean validateEmail(String validationToken, Integer validationCode) {
        // here using Objects.equal in case the stored validation token is null
        if (Objects.equals(tokenMap.get(validationCode), validationToken)) {
            tokenMap.remove(validationCode);
            return true;
        } else {
            return false;
        }
    }

    @Async
    @Scheduled(cron = "0 0 * * * *", zone = "Africa/Casablanca")
    public void sendingQueuedEmails() {
        Iterator<Map.Entry<Integer, String>> iterator = tokenMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            try {
                jwtDecoder.decode(entry.getValue());
            } catch (Exception e) {
                iterator.remove();
            }
        }
    }
}
