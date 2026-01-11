package com.deepdame.service.friendRequest;

import com.deepdame.dto.friendRequest.FriendRequestDto;
import com.deepdame.entity.FriendRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface FriendRequestService {
    void addFriendRequest(UUID senderId, UUID receiverId);
    Boolean friendRequestExists(UUID senderId, UUID receiverId);
    List<FriendRequestDto> getFriendRequests(UUID receiverId);
    void acceptFriendRequest(UUID userId, UUID friendRequestId);
    void refuseFriendRequest(UUID userId, UUID friendRequestId);
}
