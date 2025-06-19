package com.dice.auth.token.refresh.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class RefreshTokenSession {

    String id;
    Long userId;
    String tokenId;
    String osSystem;
    String browser;
    String ipAddress;
    Instant createdAt;
    Instant expiresAt;
    boolean revoked;
}
