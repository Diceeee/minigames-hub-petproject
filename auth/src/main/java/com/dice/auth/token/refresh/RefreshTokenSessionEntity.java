package com.dice.auth.token.refresh;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(schema = "public", name = "refresh_token_session", indexes = {
        @Index(columnList = "user_id"),
        @Index(columnList = "token_id", unique = true),
        @Index(columnList = "expires_at")
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenSessionEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId;

    @Column(name = "os_system")
    private String osSystem;

    @Column(name = "browser")
    private String browser;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "is_revoked", nullable = false)
    private boolean revoked = false;
} 