package com.deepdame.service.user;

import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.dto.user.UserDto;
import com.deepdame.dto.user.UserMapper;
import com.deepdame.entity.User;
import com.deepdame.exception.ConflictException;
import com.deepdame.exception.NotFoundException;
import com.deepdame.exception.Unauthorized;
import com.deepdame.repository.UserRepository;
import com.deepdame.service.username.UsernameService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.deepdame.service.email.EmailService;
import java.util.List;
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
    @Caching(put = {
            @CachePut(value = "users.email", key = "#result.email"),
            @CachePut(value = "users.id", key = "#result.id")
    })
    public UserDto changeUsername(UUID id, String newUsername) {
        // checking if the username is unique
        if (usernameService.isTaken(newUsername)) {
            throw new ConflictException("Username already taken");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        usernameService.releaseUsername(user.getUsername());
        user.setUsername(newUsername);
        userRepository.save(user);
        usernameService.reserveUsername(newUsername);

        return userMapper.toDTO(user);
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
        userRepository.save(user);
        evictUserCache(user);
    }

    @Transactional
    @Override
    public void unbanFromApp(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.updateAppBanStatus(id, false);
        userRepository.save(user);
        evictUserCache(user);

    }

    @Transactional
    @Override
    public void banFromChat(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.updateChatBanStatus(id, true);
        userRepository.save(user);
        evictUserCache(user);
    }

    @Transactional
    @Override
    public void unbanFromChat(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.updateChatBanStatus(id, false);
        userRepository.save(user);
        evictUserCache(user);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#user.id"),
            @CacheEvict(value = "users", key = "#user.email")
    })
    public void evictUserCache(User user) {
    }
}
