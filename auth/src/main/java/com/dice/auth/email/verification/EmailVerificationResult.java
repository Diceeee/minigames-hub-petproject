package com.dice.auth.email.verification;

import com.dice.auth.user.dto.User;

public record EmailVerificationResult(User verifiedUser, boolean successful) {
}
