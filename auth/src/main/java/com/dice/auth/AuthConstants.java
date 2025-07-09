package com.dice.auth;

public interface AuthConstants {

    interface Cookies {
        String ACCESS_TOKEN = "access_token";
        String REFRESH_TOKEN = "refresh_token";
    }

    interface Claims {
        String AUTHORITIES = "authorities";
        String SESSION_ID = "sessionId";
    }

    interface Headers {
        String USER_AGENT = "User-Agent";
        String X_USER_ID = "X-User-Id";
    }

    interface Uris {
        String PUBLIC_API_PREFIX = "/api/public";
        String REFRESH = PUBLIC_API_PREFIX + "/refresh";
        String LOGIN = PUBLIC_API_PREFIX + "/login";
        String REGISTER = PUBLIC_API_PREFIX + "/register";
    }
}
