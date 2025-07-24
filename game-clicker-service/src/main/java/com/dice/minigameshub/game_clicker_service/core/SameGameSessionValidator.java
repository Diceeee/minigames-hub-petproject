package com.dice.minigameshub.game_clicker_service.core;

import com.dice.minigameshub.game_clicker_service.common.UserDetails;
import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import org.springframework.stereotype.Component;

/**
 * Validates that user session that was used to start game last time is the same as current session.
 */
@Component
public class SameGameSessionValidator {

    public void validateSameGameSession(UserSaveDocument currentUserSave, UserDetails userDetails) {
        if (!currentUserSave.getLastSessionId().equals(userDetails.getSessionId())) {
            throw new ServiceException("Game was started for different session last time, please, start game again!", Error.ACTIVE_SESSION_MISMATCH);
        }
    }
}
