package com.deepdame.security;

import com.deepdame.dto.user.UserDto;
import com.deepdame.dto.user.UserMapper;
import com.deepdame.entity.User;
import com.deepdame.service.user.UserEntityService;
import com.deepdame.service.user.UserService;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        UserDto user = userService.findByEmail(email);
        return new CustomUserDetails(user);
    }
}