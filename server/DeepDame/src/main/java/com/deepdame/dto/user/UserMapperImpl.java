package com.deepdame.dto.user;

import com.deepdame.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

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
