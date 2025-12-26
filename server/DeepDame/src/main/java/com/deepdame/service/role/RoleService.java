package com.deepdame.service.role;

import com.deepdame.dto.role.RoleDto;
import com.deepdame.entity.Role;
import com.deepdame.service.CrudDtoService;

import java.util.UUID;

public interface RoleService extends CrudDtoService<UUID, RoleDto> {
}
