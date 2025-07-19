package com.dice.minigameshub.game_clicker_service.items;

import com.dice.minigameshub.game_clicker_service.achievement.UserEventsAchievementsProcessor;
import com.dice.minigameshub.game_clicker_service.achievement.dto.AchievementState;
import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import com.dice.minigameshub.game_clicker_service.items.dto.UpdateOrCreateItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemResult;
import com.dice.minigameshub.game_clicker_service.items.event.ItemPurchasedEvent;
import com.dice.minigameshub.game_clicker_service.save.UserSaveService;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemsService {


    private final UserSaveService userSaveService;
    private final ItemsRepository itemsRepository;

    public ItemDocument getItemById(String itemId) {
        return itemsRepository.findById(itemId)
                .orElseThrow(() -> new ServiceException(String.format("Item by id %s not found", itemId), Error.ITEM_NOT_FOUND));
    }

    public List<ItemDocument> getAllItems() {
        return itemsRepository.findAll();
    }

    public ItemDocument updateOrCreateItem(UpdateOrCreateItemInput input) {
        ItemDocument updatedOrCreatedItemDocument = ItemDocument.builder()
                .id(input.getItemId())
                .name(input.getName())
                .description(input.getDescription())
                .price(input.getPrice())
                .currencyIncomeIncreaseInMinute(input.getCurrencyIncomeIncreaseInMinute())
                .currencyIncomeIncreasePerClick(input.getCurrencyIncomeIncreasePerClick())
                .build();

        return itemsRepository.save(updatedOrCreatedItemDocument);
    }
}
