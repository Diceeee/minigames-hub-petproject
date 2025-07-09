package com.dice.auth.token.refresh.api.dto;

import lombok.Value;

@Value
public class RefreshTokensResponse {

    String accessToken;
    String refreshToken;
}
