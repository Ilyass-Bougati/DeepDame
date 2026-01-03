package com.deepdame.service.user;

import com.deepdame.entity.User;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users.id", key = "#id")
    public User findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.getRoles().size();
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users.all", key = "'ALL_USERS'")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users.email", key = "#email")
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        user.getRoles().size();
        return user;
    }

    @Override
    @CacheEvict(value = "users", key = "#email")
    public void updateRefreshToken(String email, String token) {
        User user = findByEmail(email);
        user.setRefreshToken(token);
        userRepository.save(user);
    }
}