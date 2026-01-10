package com.deepdame.controller.admin;

import com.deepdame.dto.generalChatMessage.GeneralChatMessageDto;
import com.deepdame.entity.User;
import com.deepdame.security.UserSecurity;
import com.deepdame.service.generalChatMessage.GeneralChatMessageService;
import com.deepdame.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/admin/chat")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER-ADMIN')")
@RequiredArgsConstructor
public class AdminChatController {

    private final GeneralChatMessageService chatMessageService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserSecurity userSecurity;

    @PostMapping("/ban-user-from-chat/{messageId}")
    @ResponseBody
    public ResponseEntity<?> banUserFromChat(@PathVariable UUID messageId, Principal principal) {
        try {
            GeneralChatMessageDto message = chatMessageService.findById(messageId);
            UUID targetUserId = message.getUser().getId();

            if (!userSecurity.canManage(targetUserId, principal.getName())) {
                log.warn("Permission denied for admin {} on sender {}", principal.getName(), targetUserId);
                return ResponseEntity.status(403).body("Insufficient hierarchical rights.");
            }

            userService.banFromChat(targetUserId);
            chatMessageService.delete(messageId);
            messagingTemplate.convertAndSend("/topic/general-chat-delete", messageId.toString());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Chat ban error: ", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/ban-user-from-app/{messageId}")
    @ResponseBody
    public ResponseEntity<?> banUserFromApp(@PathVariable UUID messageId, Principal principal) {
        try {
            GeneralChatMessageDto message = chatMessageService.findById(messageId);
            UUID targetUserId = message.getUser().getId();

            if (!userSecurity.canManage(targetUserId, principal.getName())) {
                log.warn("Permission denied for admin {} on sender {}", principal.getName(), targetUserId);
                return ResponseEntity.status(403).body("Insufficient hierarchical rights.");
            }

            userService.banFromApp(targetUserId);
            chatMessageService.delete(messageId);

            messagingTemplate.convertAndSend("/topic/general-chat-delete", messageId.toString());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("App ban error: ", e);
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

    @GetMapping
    public String viewChat(Model model) {
        List<GeneralChatMessageDto> recentMessages = chatMessageService.getGeneralChatMessages(0L, 50L);
        List<GeneralChatMessageDto> chronologicalMessages = new ArrayList<>(recentMessages);
        Collections.reverse(chronologicalMessages);

        model.addAttribute("messages", chronologicalMessages);
        return "admin/chat/general-chat";
    }
}