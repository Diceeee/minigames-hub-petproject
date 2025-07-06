package com.dice.auth.email.verification;

import com.dice.auth.user.dto.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmailVerificationResult {

    User verifiedUser;
    boolean successful;
    Error error;

    public static EmailVerificationResult error(Error error) {
        return EmailVerificationResult.builder()
                .error(error)
                .build();
    }

    public static EmailVerificationResult successful(User verifiedUser) {
        return EmailVerificationResult.builder()
                .successful(true)
                .verifiedUser(verifiedUser)
                .build();
    }

    public enum Error {
        TOKEN_NOT_FOUND,
        EMAIL_ALREADY_VERIFIED
    }
}
