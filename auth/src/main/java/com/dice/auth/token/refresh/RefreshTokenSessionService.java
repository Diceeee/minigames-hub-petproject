package com.dice.auth.token.refresh;

import com.dice.auth.token.refresh.dto.RefreshTokenSession;
import com.dice.auth.token.refresh.exception.SessionAssociatedWithRefreshTokenNotFound;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class RefreshTokenSessionService {

    private final Clock clock;
    private final RefreshTokenSessionMapper mapper;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;

    @Transactional(readOnly = true, noRollbackFor = SessionAssociatedWithRefreshTokenNotFound.class)
    public RefreshTokenSession getByTokenId(String tokenId) throws SessionAssociatedWithRefreshTokenNotFound {
        return refreshTokenSessionRepository.findByTokenId(tokenId)
                .map(mapper::mapEntity)
                .orElseThrow(() -> SessionAssociatedWithRefreshTokenNotFound.forTokenId(tokenId));
    }

    public RefreshTokenSession save(RefreshTokenSession session) {
        log.info("Saved refresh token session with token id {}", session.getTokenId());
        return mapper.mapEntity(refreshTokenSessionRepository.save(mapper.mapToEntity(session)));
    }

    public void deleteTokenByTokenId(String tokenId) {
        log.info("Deleting refresh token by id {}", tokenId);
        refreshTokenSessionRepository.deleteByTokenId(tokenId);
    }

    public void clearExpiredRefreshTokens() {
        log.info("Clearing expired refresh tokens...");
        refreshTokenSessionRepository.deleteByExpiresAtBefore(clock.instant());
    }
}
