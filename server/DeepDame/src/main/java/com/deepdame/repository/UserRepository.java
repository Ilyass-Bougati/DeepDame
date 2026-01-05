package com.deepdame.repository;

import com.deepdame.entity.User;
import lombok.NonNull;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
