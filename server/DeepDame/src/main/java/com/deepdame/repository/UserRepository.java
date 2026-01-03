package com.deepdame.repository;

import com.deepdame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    long countByBannedFromAppFalse();

    long countByBannedFromAppTrue();

    @Query("SELECT count(u) FROM User u WHERE u.createdAt >= CURRENT_DATE")
    long countNewUsersToday();
}
