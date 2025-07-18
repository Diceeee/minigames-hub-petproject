package com.dice.auth.email.verification;

import com.dice.auth.core.properties.AuthConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@AllArgsConstructor
public class EmailVerificationTokenCleaner {

    private final EmailVerificationTokenRepository tokenRepository;
    private final AuthConfigurationProperties authProperties;
    private final Clock clock;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void cleanExpiredEmailVerificationTokens() {
        log.info("Cleaning expired email verification tokens that were not used in {} minutes since created", authProperties.getExpireEmailVerificationsMinutes());
        Instant expiredBefore = clock.instant().minus(authProperties.getExpireEmailVerificationsMinutes(), ChronoUnit.MINUTES);

        tokenRepository.deleteByCreatedAtBefore(expiredBefore);
    }
}
