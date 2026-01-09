package com.deepdame.repository;

import com.deepdame.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Boolean existsByName(String name);

    Optional<Role> findByName(String superAdmin);
    List<Role> findAll();
}
