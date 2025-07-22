package com.dice.auth.token.refresh;

import com.dice.auth.common.exception.ServiceError;
import com.dice.auth.common.exception.ServiceException;
import com.dice.auth.token.refresh.dto.RefreshTokenSession;
import com.dice.auth.token.refresh.exception.SessionAssociatedWithRefreshTokenNotFound;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<RefreshTokenSession> getUserActiveSessions(Long userId) {
        log.info("User {} getting all active sessions", userId);

        return refreshTokenSessionRepository.findAllByUserId(userId).stream()
                .map(mapper::mapEntity)
                .toList();
    }

    public RefreshTokenSession save(RefreshTokenSession session) {
        log.info("Saved refresh token session with token id {}", session.getTokenId());
        return mapper.mapEntity(refreshTokenSessionRepository.save(mapper.mapToEntity(session)));
    }

    public void revokeSession(Long userId, String currentSessionId, String revokedSessionId) {
        log.info("Revoke session '{}' is called by user '{}' from session '{}'", revokedSessionId, userId, currentSessionId);

        if (currentSessionId.equals(revokedSessionId)) {
            throw new ServiceException("Revoke is called for currently used session that is not eligible",
                    ServiceError.SESSION_REVOKE_IS_NOT_ELIGIBLE_FOR_CURRENT_SESSION);
        }

        RefreshTokenSessionEntity refreshTokenSession = refreshTokenSessionRepository.findById(revokedSessionId)
                .orElseThrow(() -> new ServiceException("Can't find session by id " + revokedSessionId, ServiceError.SESSION_NOT_FOUND));

        if (refreshTokenSession.isRevoked()) {
            throw new ServiceException("Revoke is called for session that already revoked: " + revokedSessionId, ServiceError.SESSION_ALREADY_REVOKED);
        }
        if (!refreshTokenSession.getUserId().equals(userId)) {
            throw new ServiceException(String.format("Revoke is called for session '%s' by user '%d', but session does not belong to him",
                    revokedSessionId, userId), ServiceError.SESSION_DOES_NOT_BELONG_TO_USER);
        }

        refreshTokenSession.setRevoked(true);
        refreshTokenSessionRepository.save(refreshTokenSession);

        log.info("Session {} is revoked by user {}", revokedSessionId, userId);
    }

    public void deleteTokenByTokenId(String tokenId) {
        log.info("Deleting refresh token by id {}", tokenId);
        refreshTokenSessionRepository.deleteByTokenId(tokenId);
    }

    public void deleteAllUserSessions(Long userId) {
        log.info("Deleting all refresh tokens of user {}", userId);
        refreshTokenSessionRepository.deleteAllByUserId(userId);
    }

    public void clearExpiredRefreshTokens() {
        log.info("Clearing expired refresh tokens...");
        refreshTokenSessionRepository.deleteByExpiresAtBefore(clock.instant());
    }
}
