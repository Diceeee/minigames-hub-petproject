package com.dice.auth.token.refresh;

import com.dice.auth.core.exception.ApiError;
import com.dice.auth.core.exception.ApiErrorResponse;
import com.dice.auth.core.exception.ApiException;
import com.dice.auth.token.refresh.dto.RefreshTokenSession;
import com.dice.auth.token.refresh.exception.SessionAssociatedWithRefreshTokenNotFound;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

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

    public void revokeSession(Long userId, String sessionId) {
        log.info("Revoke session '{}' is called by user '{}'", sessionId, userId);

        RefreshTokenSessionEntity refreshTokenSession = refreshTokenSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException("Can't find session by id " + sessionId, ApiError.SESSION_NOT_FOUND));

        if (refreshTokenSession.isRevoked()) {
            throw new ApiException("Revoke is called for session that already revoked: " + sessionId, ApiError.SESSION_ALREADY_REVOKED);
        }
        if (!refreshTokenSession.getUserId().equals(userId)) {
            throw new ApiException(String.format("Revoke is called for session '%s' by user '%d', but session does not belong to him",
                    sessionId, userId), ApiError.SESSION_DOES_NOT_BELONG_TO_USER);
        }

        refreshTokenSession.setRevoked(true);
        refreshTokenSessionRepository.save(refreshTokenSession);

        log.info("Session {} is revoked by user {}", sessionId, userId);
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
