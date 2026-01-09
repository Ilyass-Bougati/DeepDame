package com.deepdame.security;

import com.deepdame.entity.User;
import com.deepdame.service.user.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserEntityService userEntityService;

    public boolean canManage(User targetUser, Object principal) {
        if (targetUser == null || principal == null) return false;
        String currentUserEmail;

        if (principal instanceof CustomUserDetails userDetails) {
            currentUserEmail = userDetails.getUsername();
        } else {
            currentUserEmail = principal.toString(); // Cas où c'est un String
        }

        List<String> targetRoles = targetUser.getRoles().stream()
                .map(r -> r.getName().toUpperCase())
                .toList();

        if (targetRoles.contains("SUPER-ADMIN")) {
            return false;
        }

        if (targetUser.getEmail().equals(currentUserEmail)) {
            return true;
        }
        User currentUser = userEntityService.findByEmail(currentUserEmail);
        if (currentUser == null) return false;

        List<String> currentRoles = currentUser.getRoles().stream()
                .map(r -> r.getName().toUpperCase()).toList();

        if (currentRoles.contains("SUPER-ADMIN")) return true;

        if (currentRoles.contains("ADMIN")) {
            return !targetRoles.contains("ADMIN");
        }

        return false;
    }

    public boolean canManage(UUID targetId, Object principal) {
        if (targetId == null) return false;
        return canManage(userEntityService.findById(targetId), principal);
    }

}