package com.dice.auth.registration;

import com.dice.auth.user.domain.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegistrationResult {

    String updatedAccessToken;
    User registeredUser;
    boolean isSuccessful;
    Error error;

    public enum Error {
        USERNAME_DUPLICATE,
        ALREADY_REGISTERED,
        OAUTH2_REGISTRATION_BROKEN,
    }
}
