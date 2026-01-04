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

    @Override
    public List<GeneralChatMessageDto> getGeneralChatMessages(Long limit) {
        Pageable limitPage = PageRequest.of(0, limit.intValue());
        return generalChatMessageRepository
                .findByOrderByCreatedAt(limitPage).stream()
                .map(generalChatMessageMapper::toDTO)
                .toList();
    }
}
