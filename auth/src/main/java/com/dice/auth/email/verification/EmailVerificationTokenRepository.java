package com.dice.auth.email.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {

    @Modifying
    void deleteByTokenId(String tokenId);

    Optional<EmailVerificationTokenEntity> findByTokenId(String tokenId);

    Optional<EmailVerificationTokenEntity> findByUserId(Long userId);
}
