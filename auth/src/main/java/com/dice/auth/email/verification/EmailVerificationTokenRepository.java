package com.dice.auth.email.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {

    Optional<EmailVerificationTokenEntity> findByTokenId(String tokenId);

    Optional<EmailVerificationTokenEntity> findByUserId(Long userId);

    void deleteByCreatedAtBefore(Instant before);
    void deleteAllByUserIdIn(Collection<Long> userIds);
}
