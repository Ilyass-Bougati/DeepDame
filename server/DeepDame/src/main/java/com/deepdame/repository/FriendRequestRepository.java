package com.deepdame.repository;

import com.deepdame.dto.friendRequest.FriendRequestDto;
import com.deepdame.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    Boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

    Boolean existsBySenderIdAndReceiverIdOrReceiverIdAndSenderId(UUID senderId, UUID receiverId, UUID receiverId1, UUID senderId1);

    List<FriendRequest> findByReceiverId(UUID receiverId);

    Boolean existsByReceiverIdAndId(UUID receiverId, UUID id);

}
