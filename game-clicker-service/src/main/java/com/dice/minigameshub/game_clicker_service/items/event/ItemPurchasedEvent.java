package com.dice.minigameshub.game_clicker_service.items.event;

import com.dice.minigameshub.game_clicker_service.common.event.GameEvent;
import com.dice.minigameshub.game_clicker_service.items.ItemDocument;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemPurchasedEvent implements GameEvent {
    UserSaveDocument userSave;
    ItemDocument item;
}
