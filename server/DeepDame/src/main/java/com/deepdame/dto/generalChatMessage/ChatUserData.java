package com.deepdame.dto.generalChatMessage;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserData {
    private UUID id;
    private String username;
}
