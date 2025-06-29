package com.dice.auth.email.verification;

import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class EmailVerificationService {

    private final UserService userService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public boolean verifyEmail(String tokenId) {
        Optional<EmailVerificationTokenEntity> emailVerificationTokenOpt = emailVerificationTokenRepository.findById(tokenId);
        if (emailVerificationTokenOpt.isEmpty()) {
            log.warn("Email verification for token id '{}' not successful because token is missing", tokenId);
            return false;
        }

        EmailVerificationTokenEntity emailVerificationToken = emailVerificationTokenOpt.get();
        User user = userService.getUserById(emailVerificationToken.getUserId());

        if (user.isEmailVerified()) {
            log.warn("Email verification for token id '{}' not successful because user '{}' already has verified email", tokenId, user.getId());
            return false;
        }

        userService.save(user.toBuilder()
                .emailVerified(true)
                .build());

        emailVerificationTokenRepository.deleteById(emailVerificationToken.getId());

        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String createOrRecreateEmailVerificationTokenForUser(Long userId) {
        User user = userService.getUserById(userId);
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException(String.format("User %d already has verified email!", user.getId()));
        }

        String tokenId = UUID.randomUUID().toString();
        return emailVerificationTokenRepository.save(new EmailVerificationTokenEntity(tokenId, user.getId())).getId();
    }
}
