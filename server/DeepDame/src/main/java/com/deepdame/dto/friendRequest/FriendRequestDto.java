package com.deepdame.dto.friendRequest;

import com.deepdame.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    @NotNull
    private UUID id;

    @NotNull
    private UUID receiverId;

    @NotNull
    private UserDto sender;

    @CreationTimestamp
    private LocalDateTime sentAt;
}
