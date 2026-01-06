package com.deepdame.service.friendRequest;

import com.deepdame.entity.FriendRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface FriendRequestService {
    void addFriendRequest(UUID senderId, UUID receiverId);
    Boolean friendRequestExists(UUID senderId, UUID receiverId);
}
