package com.dice.minigameshub.game_clicker_service.items.purchase;

import com.dice.minigameshub.game_clicker_service.save.UserSaveRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PurchaseItemsService {

    private final UserSaveRepository userSaveRepository;

    public PurchaseItemResult purchaseItem(PurchaseItemInput input) {

        return null;
    }
}
