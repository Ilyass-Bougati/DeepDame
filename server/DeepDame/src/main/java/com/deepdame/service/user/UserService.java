package com.deepdame.service.user;

import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.dto.user.UserDto;
import com.deepdame.entity.User;
import com.deepdame.service.CrudDtoService;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface UserService extends CrudDtoService<UUID, UserDto> {
    UserDto register(RegisterRequest request);
    List<User> searchUsers(String keyword);
    UserDto findByEmail(@NonNull String email);
    Boolean areFriends(UUID userId, UUID friendId);
    void sendFriendInvitation(UUID userId, UUID friendId);
    void logout(UUID userId);
}
