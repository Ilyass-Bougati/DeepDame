package com.deepdame.controller;

import com.deepdame.dto.auth.LoginRequest;
import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUser_WhenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Akira",
                "akira@deepdame.com",
                "password123"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convert to JSON

                // 3. Verify the Response
                .andExpect(status().isOk());

        var savedUser = userRepository.findByEmail("akira@deepdame.com")
                .orElseThrow(() -> new AssertionError("User was not saved to the DB!"));

        Assertions.assertEquals("Akira", savedUser.getUsername());
        Assertions.assertEquals("akira@deepdame.com", savedUser.getEmail());

        Assertions.assertNotEquals("password123", savedUser.getPassword(),
                "Bro, are you saving passwords in plain text? Use BCrypt.");
    }

    @Test
    void shouldFail_WhenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Akira",
                "not-an-email",
                "password123"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFail_WhenUserAlreadyExists() throws Exception {
        RegisterRequest firstRequest = new RegisterRequest("Akira", "duplicate@test.com", "pass");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        RegisterRequest duplicateRequest = new RegisterRequest("Imposter", "duplicate@test.com", "pass");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))

                .andExpect(status().is4xxClientError());
    }


    @Test
    void shouldLoginSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Akira",
                "login.test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest("login.test@example.com", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    void shouldFailLogin_WhenCredentialsAreInvalid() throws Exception {
        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrongpass");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldFailLogin_WhenRequestIsInvalid() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is4xxClientError());
    }
}
