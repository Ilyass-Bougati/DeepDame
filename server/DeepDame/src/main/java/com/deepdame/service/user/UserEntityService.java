package com.deepdame.service.user;

import com.deepdame.entity.User;
import com.deepdame.service.CrudEntityService;

import java.util.UUID;

public interface UserEntityService extends CrudEntityService<User, UUID> {
    User findByEmail(String email);
    void updateRefreshToken(String email, String token);
}