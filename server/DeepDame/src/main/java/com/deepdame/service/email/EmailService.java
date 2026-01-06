package com.deepdame.service.email;

public interface EmailService {
    void passwordForgottenEmail(String email, Integer validationCode);
}
