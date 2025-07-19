package com.dice.minigameshub.game_clicker_service.core.event;

import com.dice.minigameshub.game_clicker_service.common.event.GameEvent;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserStartedGameEvent implements GameEvent {
    UserSaveDocument userSave;
}
