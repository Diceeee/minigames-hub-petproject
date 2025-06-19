package com.dice.auth.registration;

import com.dice.auth.user.dto.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegistrationResult {

    User registeredUser;
    boolean isSuccessful;
    String errorId;
}
