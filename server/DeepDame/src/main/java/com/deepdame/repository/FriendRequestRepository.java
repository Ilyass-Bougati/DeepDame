package com.deepdame.repository;

import com.deepdame.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    Boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

    Boolean existsBySenderIdAndReceiverIdOrReceiverIdAndSenderId(UUID senderId, UUID receiverId, UUID receiverId1, UUID senderId1);
}
