package com.deepdame.service.user;

import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.dto.user.UserDto;
import com.deepdame.entity.User;
import com.deepdame.service.CrudDtoService;

import java.util.List;
import java.util.UUID;

public interface UserService extends CrudDtoService<UUID, UserDto> {
    UserDto register(RegisterRequest request);
    List<User> searchUsers(String keyword);
}
