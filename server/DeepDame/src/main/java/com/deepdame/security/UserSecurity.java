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

    public boolean canManage(UUID targetId, CustomUserDetails currentUser) {
        if (targetId == null || currentUser == null) return false;

        User targetUser = userEntityService.findById(targetId);
        if (targetUser == null) return false;

        List<String> targetRoles = targetUser.getRoles().stream()
                .map(r -> r.getName().toUpperCase())
                .toList();

        if (targetRoles.contains("SUPER-ADMIN")) {
            return false;
        }

        if (targetUser.getEmail().equals(currentUser.getUsername())) {
            return true;
        }

        List<String> currentRoles = currentUser.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", "").toUpperCase())
                .toList();

        if (currentRoles.contains("SUPER-ADMIN")) return true;

        if (currentRoles.contains("ADMIN")) {
            return !targetRoles.contains("ADMIN");
        }

        return false;
    }
}