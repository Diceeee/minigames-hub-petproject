package com.dice.auth.token.refresh;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class RefreshTokenSessionCleanupScheduler {

    private final RefreshTokenSessionService refreshTokenSessionService;

    /**
     * Clean up expired refresh token sessions every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredSessions() {
        try {
            log.debug("Starting scheduled cleanup of expired refresh token sessions");
            refreshTokenSessionService.clearExpiredRefreshTokens();
            log.debug("Completed scheduled cleanup of expired refresh token sessions");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup of expired sessions", e);
        }
    }
} 