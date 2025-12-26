package com.deepdame.dto.role;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoleDto {
    @EqualsAndHashCode.Include
    private UUID id;

    @NotEmpty
    private String name;
}
