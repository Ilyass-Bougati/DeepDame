package com.deepdame;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.dto.generalChatMessage.GeneralChatMessageMapper;
import com.deepdame.dto.generalChatMessage.GeneralChatMessageMapperImpl;
import com.deepdame.dto.role.RoleDto;
import com.deepdame.dto.role.RoleMapper;
import com.deepdame.dto.role.RoleMapperImpl;
import com.deepdame.dto.user.UserDto;
import com.deepdame.dto.user.UserMapper;
import com.deepdame.dto.user.UserMapperImpl;
import com.deepdame.entity.GeneralChatMessage;
import com.deepdame.entity.Role;
import com.deepdame.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;


public class MapperTest {
    private final UserMapper userMapper = new UserMapperImpl();
    private final RoleMapper roleMapper = new RoleMapperImpl();
    private final GeneralChatMessageMapper generalChatMessageMapper = new GeneralChatMessageMapperImpl();

    @Test
    public void userMapperTest() {
        User mockUser = User.builder()
                .id(new UUID(0, 0))
                .username("mock.user")
                .email("mock.email@gmail.com")
                .password("mock.password")
                .bannedFromApp(true)
                .build();

        UserDto dto = userMapper.toDTO(mockUser);

        // checking the mapping
        Assertions.assertEquals(mockUser.getEmail(), dto.getEmail());
        Assertions.assertEquals(mockUser.getUsername(), dto.getUsername());
        Assertions.assertEquals(mockUser.getId(), dto.getId());
        Assertions.assertEquals(mockUser.getBannedFromApp(), true);
        Assertions.assertEquals(mockUser.getBannedFromChat(), false);
        Assertions.assertEquals(mockUser.getEmailValidated(), false);
    }

    @Test
    public void roleMapperTest() {
        Role mockRole = Role.builder()
                .id(new UUID(0, 0))
                .name("mockRole")
                .build();

        RoleDto dto = roleMapper.toDTO(mockRole);

        Assertions.assertEquals(mockRole.getId(), dto.getId());
        Assertions.assertEquals(mockRole.getName(), dto.getName());
    }

    @Test
    public void generalChatMessageMapperTest() {
        User user = new User();
        GeneralChatMessage mockGeneralChatMessage = GeneralChatMessage.builder()
                .id(new UUID(0, 0))
                .message("mock message")
                .user(user)
                .build();

        GeneralChatMessageDto dto = generalChatMessageMapper.toDTO(mockGeneralChatMessage);

        Assertions.assertEquals(mockGeneralChatMessage.getId(), dto.getId());
        Assertions.assertEquals(mockGeneralChatMessage.getMessage(), dto.getMessage());
        Assertions.assertEquals(mockGeneralChatMessage.getUser(), user);
    }
}
