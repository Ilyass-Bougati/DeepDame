package com.deepdame.service.generalChatMessage;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.service.CrudDtoService;

import java.util.List;
import java.util.UUID;

public interface GeneralChatMessageService extends CrudDtoService<UUID, GeneralChatMessageDto> {
    List<GeneralChatMessageDto> getGeneralChatMessages(Long pageNumber, Long limit);
}
