package com.deepdame.dto.role;

import com.deepdame.entity.Role;

public class RoleMapperImpl implements RoleMapper {
    @Override
    public RoleDto toDTO(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    /**
     * This function doesn't set the users of the roles
     * TODO : do that
     * @param roleDto
     * @return
     */
    @Override
    public Role toEntity(RoleDto roleDto) {
        return Role.builder()
                .id(roleDto.getId())
                .name(roleDto.getName())
                .build();
    }
}
