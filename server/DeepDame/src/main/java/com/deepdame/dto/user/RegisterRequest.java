package com.deepdame.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public void setEmail(String email) {
        this.email = email == null ? null : email.toLowerCase().strip();
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }
}