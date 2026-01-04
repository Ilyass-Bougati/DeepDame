package com.deepdame.repository;

import com.deepdame.entity.GeneralChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GeneralChatMessageRepository extends JpaRepository<GeneralChatMessage, UUID> {
    List<GeneralChatMessage> findByOrderByCreatedAt(Pageable limitPage);
}
