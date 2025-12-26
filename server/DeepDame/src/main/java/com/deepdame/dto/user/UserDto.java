package com.deepdame.dto.user;


import com.deepdame.dto.role.RoleDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {
    @EqualsAndHashCode.Include
    private UUID id;

    @NotEmpty
    private String username;

    @NotEmpty
    @Email
    private String email;

    @NotNull
    private Boolean emailValidated;

    @NotNull
    private Boolean bannedFromChat;

    @NotNull
    private Boolean bannedFromApp;
    private Set<RoleDto> roles;

    @NotNull
    private LocalDateTime createdAt;
}
