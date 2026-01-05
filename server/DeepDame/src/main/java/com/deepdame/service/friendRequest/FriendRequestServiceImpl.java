package com.deepdame.service.friendRequest;

import com.deepdame.entity.FriendRequest;
import com.deepdame.entity.User;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.FriendRequestRepository;
import com.deepdame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Override
    public void addFriendRequest(UUID userId, UUID friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found!"));
        User friend = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(user)
                .receiver(friend)
                .build();

        friendRequestRepository.save(friendRequest);
    }
}
