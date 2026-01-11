package com.deepdame.controller;

import com.deepdame.dto.friendRequest.FriendRequestDto;
import com.deepdame.repository.FriendRequestRepository;
import com.deepdame.security.CustomUserDetails;
import com.deepdame.service.friendRequest.FriendRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/friend-request")
@RequiredArgsConstructor
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    @GetMapping("/")
    private ResponseEntity<List<FriendRequestDto>> getFriendRequests(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(friendRequestService.getFriendRequests(principal.getUser().getId()));
    }

    @PostMapping("/accept/{id}")
    private void acceptFriendRequest(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable UUID id) {
        friendRequestService.acceptFriendRequest(principal.getUser().getId(), id);
    }

    @PostMapping("/refuse/{id}")
    private void refuseFriendRequest(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable UUID id) {
        friendRequestService.refuseFriendRequest(principal.getUser().getId(), id);
    }
}
