package com.chung.taskcrud.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="email_verification_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmailVerificationToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable=false, unique=true, length=128)
    private String token;

    @Column(nullable=false)
    private Instant expiresAt;

    private Instant usedAt;

    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public boolean isUsed() { return usedAt != null; }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
}
