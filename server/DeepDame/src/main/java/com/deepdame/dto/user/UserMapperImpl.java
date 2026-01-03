package com.deepdame.dto.user;

import com.deepdame.dto.role.RoleMapper;
import com.deepdame.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final RoleMapper roleMapper;

    /**
     * This function doesn't set the roles of the user
     * TODO : fix that
     * @param user
     * @return
     */
    @Override
    public UserDto toDTO(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bannedFromChat(user.getBannedFromChat())
                .bannedFromApp(user.getBannedFromApp())
                .emailValidated(user.getEmailValidated())
                .roles(user.getRoles().stream().map(roleMapper::toDTO).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * This function doesn't set the friends, roles and messages
     * TODO : fix that (and think before doing so)
     * @param userDto
     * @return
     */
    @Override
    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .bannedFromChat(userDto.getBannedFromChat())
                .bannedFromApp(userDto.getBannedFromApp())
                .emailValidated(userDto.getEmailValidated())
                .createdAt(userDto.getCreatedAt())
                .build();
    }
}
