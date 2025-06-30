package com.dice.auth.email.verification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(schema = "public", name = "email_verification_token", indexes = {
        @Index(columnList = "user_id", unique = true),
        @Index(columnList = "token_id", unique = true)
})
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public EmailVerificationTokenEntity(String tokenId, Long userId, Instant createdAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
