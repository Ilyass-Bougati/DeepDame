package com.deepdame.repository;

import com.deepdame.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
  
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u.username FROM User u")
    Stream<String> findAllUsernames();

    long countByBannedFromAppFalse();

    long countByBannedFromAppTrue();

    @Query("SELECT count(u) FROM User u WHERE u.createdAt >= CURRENT_DATE")
    long countNewUsersToday();

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.friends f WHERE u.id = :userId AND f.id = :friendId")
    boolean areFriends(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.refreshToken = NULL WHERE u.id = :userId")
    void invalidateRefreshToken(@Param("userId") UUID userId);

    Optional<User> findByRefreshToken(String refreshToken);

    @Modifying
    @Query("UPDATE User u SET u.bannedFromApp = :status, u.refreshToken = null WHERE u.id = :id")
    void updateAppBanStatus(@Param("id") UUID id, @Param("status") boolean status);

    @Modifying
    @Query("UPDATE User u SET u.bannedFromChat = :status WHERE u.id = :id")
    void updateChatBanStatus(@Param("id") UUID id, @Param("status") boolean status);
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.email = :email")
    void changePasswordByEmail(@Param("email") String email, @Param("newPassword") String newPassword);
}
