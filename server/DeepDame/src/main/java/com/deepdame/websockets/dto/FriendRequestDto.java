package com.deepdame.websockets.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    private UUID senderId;
    private UUID receiverId;
    private LocalDateTime date = LocalDateTime.now();
}
