package com.deepdame.dto.generalChatMessage;

import com.deepdame.entity.GeneralChatMessage;
import com.deepdame.service.user.UserEntityService;
import com.deepdame.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeneralChatMessageMapperImpl implements GeneralChatMessageMapper {
    private final UserEntityService userEntityService;

    @Override
    public GeneralChatMessageDto toDTO(GeneralChatMessage generalChatMessage) {
        return GeneralChatMessageDto.builder()
                .id(generalChatMessage.getId())
                .message(generalChatMessage.getMessage())
                .user(ChatUserData.builder()
                        .id(generalChatMessage.getUser().getId())
                        .username(generalChatMessage.getUser().getUsername())
                        .build())
                .createdAt(generalChatMessage.getCreatedAt())
                .build();
    }

    @Override
    public GeneralChatMessage toEntity(GeneralChatMessageDto generalChatMessageDto) {
        return GeneralChatMessage.builder()
                .id(generalChatMessageDto.getId())
                .message(generalChatMessageDto.getMessage())
                .user(userEntityService.findById(generalChatMessageDto.getUser().getId()))
                .createdAt(generalChatMessageDto.getCreatedAt())
                .build();
    }
}
