package com.dice.auth.user.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class UserMeResponse {

    Long id;
    String sessionId;
    String email;
    String username;
    boolean emailVerified;
    boolean registered;
    Set<String> authorities;
}
