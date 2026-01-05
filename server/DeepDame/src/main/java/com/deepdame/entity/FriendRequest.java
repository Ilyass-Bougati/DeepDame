package com.deepdame.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FriendRequest {
    @Id
    private UUID id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    @CreationTimestamp
    private LocalDateTime sentAt;
}
