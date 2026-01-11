package com.deepdame.service.friendRequest;

import com.deepdame.dto.friendRequest.FriendRequestDto;
import com.deepdame.dto.friendRequest.FriendRequestMapper;
import com.deepdame.entity.FriendRequest;
import com.deepdame.entity.User;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.FriendRequestRepository;
import com.deepdame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRequestMapper friendRequestMapper;
    private final UserRepository userRepository;

    @Override
    public void addFriendRequest(UUID senderId, UUID receiverId) {
        User user = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found!"));
        User friend = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(user)
                .receiver(friend)
                .build();

        friendRequestRepository.save(friendRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean friendRequestExists(UUID senderId, UUID receiverId) {
        return friendRequestRepository.existsBySenderIdAndReceiverIdOrReceiverIdAndSenderId(senderId, receiverId, senderId, receiverId);
    }

    @Override
    public List<FriendRequestDto> getFriendRequests(UUID receiverId) {
        return friendRequestRepository.findByReceiverId(receiverId)
                .stream().map(friendRequestMapper::toDTO).toList();
    }

    @Override
    public void acceptFriendRequest(UUID userId, UUID friendRequestId) {
        Boolean exists = friendRequestRepository.existsByReceiverIdAndId(userId, friendRequestId);
        if (exists) {
            // Getting the sender and receiver
            FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                    .orElseThrow(() -> new NotFoundException("Friend request not found!"));
            User sender = userRepository.findById(friendRequest.getSender().getId())
                    .orElseThrow(() -> new NotFoundException("User not found!"));
            User receiver = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found!"));

            sender.getFriends().add(receiver);
            receiver.getFriends().add(sender);

            // We don't need to save on both since we have cascade enabled
            userRepository.save(sender);
            userRepository.save(receiver);

            friendRequestRepository.delete(friendRequest);
        } else {
            // not reveling information
            throw new NotFoundException("Friend request not found!");
        }
    }

    @Override
    public void refuseFriendRequest(UUID userId, UUID friendRequestId) {
        Boolean exists = friendRequestRepository.existsByReceiverIdAndId(userId, friendRequestId);
        if (exists) {
            // Getting the sender and receiver
            FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                    .orElseThrow(() -> new NotFoundException("Friend request not found!"));
            friendRequestRepository.delete(friendRequest);

            // TODO : we need to add a way so that we can't spam send friend request to a person

        } else {
            // not reveling information
            throw new NotFoundException("Friend request not found!");
        }
    }
}
