package com.deepdame.controller;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.service.generalChatMessage.GeneralChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/general-chat")
@RequiredArgsConstructor
public class GeneralChatRestController {
    private final GeneralChatMessageService generalChatMessageService;

    @GetMapping("/")
    ResponseEntity<List<GeneralChatMessageDto>> getGeneralChatMessages() {
        return ResponseEntity.ok(generalChatMessageService.getGeneralChatMessages(50L));
    }
}
