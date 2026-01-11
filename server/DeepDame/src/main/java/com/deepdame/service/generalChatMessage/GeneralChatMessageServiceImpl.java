package com.deepdame.service.generalChatMessage;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.dto.generalChatMessage.GeneralChatMessageMapper;
import com.deepdame.entity.GeneralChatMessage;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.GeneralChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GeneralChatMessageServiceImpl implements GeneralChatMessageService {
    private final GeneralChatMessageRepository generalChatMessageRepository;
    private final GeneralChatMessageMapper generalChatMessageMapper;

    @Override
    public GeneralChatMessageDto save(GeneralChatMessageDto generalChatMessageDto) {
        GeneralChatMessage generalChatMessage = generalChatMessageMapper.toEntity(generalChatMessageDto);
        return generalChatMessageMapper.toDTO(generalChatMessageRepository.save(generalChatMessage));
    }

    @Override
    public GeneralChatMessageDto update(GeneralChatMessageDto generalChatMessageDto) {
        GeneralChatMessage generalChatMessage = generalChatMessageRepository.findById(generalChatMessageDto.getId())
                .orElseThrow(() -> new NotFoundException("GeneralChatMessage not found"));

        generalChatMessage.setMessage(generalChatMessageDto.getMessage());
        return generalChatMessageMapper.toDTO(generalChatMessage);
    }

    @Override
    public void delete(UUID uuid) {
        generalChatMessageRepository.deleteById(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneralChatMessageDto findById(UUID id) {
        return generalChatMessageRepository.findById(id)
                .map(generalChatMessageMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("GeneralChatMessage not found"));
    }

    /**
     * This function returns a page of messages, for example of we have 3 messages and the limit
     * is 2, the 0th page will have the latest message, while the 1st page will have the 2 other.
     * This logic was implemented to keep messaging consistent in the front end.
     *
     * @param pageNumber this is the number of the page
     * @param limit this is the number of messages on each page
     * @return a list of messages sorted from oldest to newest
     */
    @Override
    public List<GeneralChatMessageDto> getGeneralChatMessages(Long pageNumber, Long limit) {
        Long len = generalChatMessageRepository.count() / limit;
        int page = len.intValue() - pageNumber.intValue();

        if (page < 0) {
            return Collections.emptyList();
        }

        Pageable limitPage = PageRequest.of(page, limit.intValue());
        return generalChatMessageRepository
                .findByOrderByCreatedAt(limitPage).stream()
                .map(generalChatMessageMapper::toDTO)
                .toList();
    }
}
