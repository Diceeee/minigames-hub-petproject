package com.dice.auth;

public interface AuthConstants {

    interface Cookies {
        String ACCESS_TOKEN = "access_token";
        String REFRESH_TOKEN = "refresh_token";
    }

    interface Claims {
        String AUTHORITIES = "authorities";
    }

    interface Headers {
        String USER_AGENT = "User-Agent";
        String X_USER_ID = "X-User-Id";
    }

    interface Uris {
        String HOME = "/";
        String REFRESH = "/refresh";
        String LOGIN = "/login";
        String LOGOUT = REFRESH + "/logout";
        String REGISTER = "/register";
    }
}
