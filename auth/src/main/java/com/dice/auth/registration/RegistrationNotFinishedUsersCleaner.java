package com.dice.auth.registration;

import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.email.verification.EmailVerificationTokenRepository;
import com.dice.auth.token.refresh.RefreshTokenSessionRepository;
import com.dice.auth.user.UserEntity;
import com.dice.auth.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class RegistrationNotFinishedUsersCleaner {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final AuthConfigurationProperties authProperties;
    private final UserRepository userRepository;
    private final Clock clock;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void cleanUsersThatNotFinishedRegistrationForLongTime() {

        Instant expiredBefore = clock.instant().minus(authProperties.getExpireNotRegisteredUsersMinutes(), ChronoUnit.MINUTES);
        List<UserEntity> allNotRegisteredUsersOlderThan = userRepository.getAllNotRegisteredUsersOlderThan(expiredBefore);
        Set<Long> userIds = allNotRegisteredUsersOlderThan.stream().map(UserEntity::getId).collect(Collectors.toSet());

        if (!userIds.isEmpty()) {
            log.info("Cleaning users that not finished registration in {} minutes since they started it: '{}'",
                    authProperties.getExpireNotRegisteredUsersMinutes(), userIds);

            emailVerificationTokenRepository.deleteAllByUserIdIn(userIds);
            refreshTokenSessionRepository.deleteAllByUserIdIn(userIds);
            userRepository.deleteAllByIdInBatch(userIds);
        }
    }
}
