package com.deepdame.dto.friendRequest;

import com.deepdame.dto.user.UserMapper;
import com.deepdame.entity.FriendRequest;
import com.deepdame.service.user.UserEntityService;
import com.deepdame.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendRequestMapperImpl implements  FriendRequestMapper {
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserEntityService userEntityService;

    @Override
    public FriendRequestDto toDTO(FriendRequest friendRequest) {
        return FriendRequestDto.builder()
                .id(friendRequest.getId())
                .sender(userMapper.toDTO(friendRequest.getSender()))
                .sentAt(friendRequest.getSentAt())
                .build();
    }

    /**
     * This isn't really made to be used, if you'll use it. Make sure to test it beforehand
     * @param friendRequestDto the dto to be mapped to an entity
     * @return the entity
     */
    @Override
    public FriendRequest toEntity(FriendRequestDto friendRequestDto) {
        return FriendRequest.builder()
                .id(friendRequestDto.getId())
                .receiver(userEntityService.findById(friendRequestDto.getReceiverId()))
                .sender(userMapper.toEntity(friendRequestDto.getSender()))
                .sentAt(friendRequestDto.getSentAt())
                .build();
    }
}
