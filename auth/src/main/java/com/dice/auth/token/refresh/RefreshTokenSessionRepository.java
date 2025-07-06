package com.dice.auth.token.refresh;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSessionEntity, String> {

    @Transactional(readOnly = true)
    Optional<RefreshTokenSessionEntity> findByTokenId(String tokenId);

    @Transactional(readOnly = true)
    List<RefreshTokenSessionEntity> findAllByUserId(Long userId);

    @Modifying
    void deleteByExpiresAtBefore(Instant now);

    @Modifying
    void deleteByTokenId(String tokenId);
}
