package com.deepdame.dto.generalChatMessage;

import com.deepdame.entity.GeneralChatMessage;

public class GeneralChatMessageMapperImpl implements GeneralChatMessageMapper {
    @Override
    public GeneralChatMessageDto toDTO(GeneralChatMessage generalChatMessage) {
        return GeneralChatMessageDto.builder()
                .id(generalChatMessage.getId())
                .message(generalChatMessage.getMessage())
                .createdAt(generalChatMessage.getCreatedAt())
                .build();
    }

    @Override
    public GeneralChatMessage toEntity(GeneralChatMessageDto generalChatMessageDto) {
        return GeneralChatMessage.builder()
                .id(generalChatMessageDto.getId())
                .message(generalChatMessageDto.getMessage())
                .createdAt(generalChatMessageDto.getCreatedAt())
                .build();
    }
}
