package com.deepdame.service.user;

import com.deepdame.dto.user.RegisterRequest;
import com.deepdame.dto.user.UserDto;
import com.deepdame.dto.user.UserMapper;
import com.deepdame.entity.User;
import com.deepdame.exception.ConflictException;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.UserRepository;
import com.deepdame.service.username.UsernameService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
