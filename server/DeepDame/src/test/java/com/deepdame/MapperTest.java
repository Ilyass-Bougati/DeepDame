package com.deepdame;

import com.deepdame.dto.generalChatMessage.ChatUserData;
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
import com.deepdame.service.user.UserEntityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.when;

public class MapperTest {

    private final RoleMapper roleMapper = new RoleMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl(roleMapper);

    private GeneralChatMessageMapper generalChatMessageMapper;
    private UserEntityService userEntityService;

    @BeforeEach
    public void setup() {
        userEntityService = Mockito.mock(UserEntityService.class);
        generalChatMessageMapper = new GeneralChatMessageMapperImpl(userEntityService);
    }

    @Test
    public void userMapperTest() {
        User mockUser = User.builder()
                .id(UUID.randomUUID())
                .username("mock.user")
                .email("mock.email@gmail.com")
                .password("mock.password")
                .bannedFromApp(true)
                .bannedFromChat(false)
                .emailValidated(false)
                .build();

        UserDto dto = userMapper.toDTO(mockUser);

        Assertions.assertEquals(mockUser.getEmail(), dto.getEmail());
        Assertions.assertEquals(mockUser.getUsername(), dto.getUsername());
        Assertions.assertEquals(mockUser.getId(), dto.getId());
        Assertions.assertEquals(true, dto.getBannedFromApp());
        Assertions.assertEquals(false, dto.getBannedFromChat());
        Assertions.assertEquals(false, dto.getEmailValidated());
    }

    @Test
    public void roleMapperTest() {
        Role mockRole = Role.builder()
                .id(UUID.randomUUID())
                .name("mockRole")
                .build();

        RoleDto dto = roleMapper.toDTO(mockRole);

        Assertions.assertEquals(mockRole.getId(), dto.getId());
        Assertions.assertEquals(mockRole.getName(), dto.getName());
    }

    @Test
    public void generalChatMessageToDtoTest() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("testUser").build();

        GeneralChatMessage mockGeneralChatMessage = GeneralChatMessage.builder()
                .id(UUID.randomUUID())
                .message("mock message")
                .user(user) // User must have an ID or mapper will throw NPE
                .build();

        GeneralChatMessageDto dto = generalChatMessageMapper.toDTO(mockGeneralChatMessage);

        Assertions.assertEquals(mockGeneralChatMessage.getId(), dto.getId());
        Assertions.assertEquals(mockGeneralChatMessage.getMessage(), dto.getMessage());
        // The DTO has userId (UUID), not User object
        Assertions.assertEquals(mockGeneralChatMessage.getUser().getId(), dto.getUser().getId());
    }

    @Test
    public void generalChatMessageToEntityTest() {
        // Prepare data
        UUID userId = UUID.randomUUID();
        User mockUser = User.builder().id(userId).username("foundUser").build();

        GeneralChatMessageDto dto = GeneralChatMessageDto.builder()
                .id(UUID.randomUUID())
                .message("test message")
                .user(ChatUserData.builder()
                        .id(mockUser.getId())
                        .build())
                .build();

        // TEACH the mock service what to do when called
        when(userEntityService.findById(userId)).thenReturn(mockUser);

        // Run the mapper
        GeneralChatMessage entity = generalChatMessageMapper.toEntity(dto);

        // Verify results
        Assertions.assertEquals(dto.getId(), entity.getId());
        Assertions.assertEquals(dto.getMessage(), entity.getMessage());
        Assertions.assertEquals(mockUser, entity.getUser()); // Verify the service was called and sender set
    }
}