package com.deepdame.service.user;

import com.deepdame.entity.User;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.UserRepository;
import com.deepdame.service.CrudEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    public void updateRefreshToken(String email, String token) {
        User user = findByEmail(email);
        user.setRefreshToken(token);
        userRepository.save(user);
    }
}