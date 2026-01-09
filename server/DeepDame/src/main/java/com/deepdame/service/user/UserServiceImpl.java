package com.deepdame.service.user;

import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.dto.user.UserDto;
import com.deepdame.dto.user.UserMapper;
import com.deepdame.entity.Role;
import com.deepdame.entity.User;
import com.deepdame.exception.ConflictException;
import com.deepdame.exception.NotFoundException;
import com.deepdame.exception.Unauthorized;
import com.deepdame.repository.RoleRepository;
import com.deepdame.repository.UserRepository;
import com.deepdame.service.username.UsernameService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.deepdame.service.email.EmailService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsernameService usernameService;
    private final EmailService emailService;
    private final CacheManager cacheManager;
    private final RoleRepository roleRepository;

    @Override
    @Caching(put = {
            @CachePut(value = "users.email", key = "#result.email"),
            @CachePut(value = "users.id", key = "#result.id")
    })
    public UserDto save(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDTO(userRepository.save(user));
    }

    /**
     * Note that this method doesn't update the email or the password
     * @param userDto the new user data
     * @return the new registered user data
     */
    @Override
    @Caching(put = {
            @CachePut(value = "users.email", key = "#result.email"),
            @CachePut(value = "users.id", key = "#result.id")
    })
    public UserDto update(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setUsername(userDto.getUsername());
        user.setBannedFromChat(userDto.getBannedFromChat());
        user.setBannedFromApp(userDto.getBannedFromApp());
        user.setEmailValidated(userDto.getEmailValidated());
        // TODO : changing password
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "users.email", key = "#result.email"),
            @CachePut(value = "users.id", key = "#result.id")
    })
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("This email address is already in use by another account.");
        }
        User user = User.builder()
                .username(request.getUsername() == null ? null : request.getUsername().toLowerCase().trim())
                .email(request.getEmail() == null ? null : request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        usernameService.reserveUsername(savedUser.getUsername());
        emailService.welcomeEmail(savedUser.getEmail(), savedUser.getUsername());
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Cacheable(value = "users.email", key = "#email")
    public UserDto findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public Boolean areFriends(UUID userId, UUID friendId) {
        return userRepository.areFriends(userId, friendId);
    }

    @Override
    public void sendFriendInvitation(UUID userId, UUID friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.getReceivedFriendInvitations().add(friend);
    }

    @Override
    public void logout(UUID userId) {
        userRepository.invalidateRefreshToken(userId);
    }

    @Override
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Unauthorized("Invalid Credentials");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void changePassword(String email, String newPassword) {
        userRepository.changePasswordByEmail(email, passwordEncoder.encode(newPassword));
    }

    @Override
    public void delete(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users.id", key = "#id")
    public UserDto findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        }
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void banFromApp(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.updateAppBanStatus(id, true);

        this.manualCacheEvict(user);
    }

    @Transactional
    @Override
    public void unbanFromApp(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.updateAppBanStatus(id, false);

        this.manualCacheEvict(user);
    }

    @Transactional
    @Override
    public void banFromChat(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.updateChatBanStatus(id, true);

        this.manualCacheEvict(user);
    }

    @Transactional
    @Override
    public void unbanFromChat(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.updateChatBanStatus(id, false);

        this.manualCacheEvict(user);
    }

    private void manualCacheEvict(User user) {
        if (cacheManager != null) {
            var emailCache = cacheManager.getCache("users.email");
            var idCache = cacheManager.getCache("users.id");

            if (emailCache != null) emailCache.evict(user.getEmail());
            if (idCache != null) idCache.evict(user.getId());
        }
    }

    @Override
    @Transactional
    public void updateUserRoles(UUID userId, List<UUID> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<Role> newRoles = new HashSet<>(roleRepository.findAllById(roleIds));
        boolean hasSuperBefore = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("SUPER-ADMIN"));
        boolean wantsSuperNow = newRoles.stream()
                .anyMatch(r -> r.getName().equals("SUPER-ADMIN"));

        if (!hasSuperBefore && wantsSuperNow) {
            throw new IllegalStateException("Security Violation: The SUPER-ADMIN role cannot be granted to new users.");
        }

        if (hasSuperBefore && !wantsSuperNow) {
            throw new IllegalStateException("System Integrity: The SUPER-ADMIN role is permanent and cannot be revoked.");
        }

        user.setRoles(newRoles);
        userRepository.save(user);
    }
}
