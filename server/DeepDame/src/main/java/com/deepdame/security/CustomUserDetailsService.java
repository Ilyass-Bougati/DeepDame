package com.deepdame.security;

import com.deepdame.entity.User;
import com.deepdame.service.user.UserEntityService;
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

    private final UserEntityService userEntityService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userEntityService.findByEmail(email);
        return new CustomUserDetails(user);
    }
}