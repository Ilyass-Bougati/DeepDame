package com.deepdame.dto.auth;

import jakarta.validation.constraints.NotEmpty;

public record ChangePasswordRequest(@NotEmpty String oldPassword, @NotEmpty String newPassword) {}