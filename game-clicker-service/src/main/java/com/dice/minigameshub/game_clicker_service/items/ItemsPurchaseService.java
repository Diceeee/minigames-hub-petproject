package com.dice.minigameshub.game_clicker_service.items;

import com.dice.minigameshub.game_clicker_service.achievement.UserEventsAchievementsProcessor;
import com.dice.minigameshub.game_clicker_service.achievement.dto.AchievementState;
import com.dice.minigameshub.game_clicker_service.achievement.util.AchievementStatesUtil;
import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
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
public class ItemsPurchaseService {

    private final UserEventsAchievementsProcessor userEventsAchievementsProcessor;
    private final UserSaveService userSaveService;
    private final ItemsService itemsService;

    public PurchaseItemResult purchaseItem(PurchaseItemInput input) {
        UserSaveDocument userSave = userSaveService.getUserSave(input.getUserDetails().getUserId());
        if (userSave.getPurchasedItemsIds().contains(input.getItemId())) {
            throw new ServiceException(String.format("Item %s is already purchased by user %d", input.getItemId(), input.getUserDetails().getUserId()),
                    com.dice.minigameshub.game_clicker_service.common.exception.Error.ITEM_ALREADY_PURCHASED);
        }

        ItemDocument item = itemsService.getItemById(input.getItemId());
        if (item.getPrice() > userSave.getCurrency()) {
            throw new ServiceException(String.format("User %d has only %d currency while item %s price is %d",
                    input.getUserDetails().getUserId(), userSave.getCurrency(), input.getItemId(), item.getPrice()), Error.NOT_ENOUGH_CURRENCY);
        }

        userSave.setCurrency(userSave.getCurrency() - item.getPrice());

        int currencyIncomePerClickBefore = userSave.getCurrencyIncomePerClick();
        int currencyIncomePerMinuteBefore = userSave.getCurrencyIncomePerMinute();
        userSave.setCurrencyIncomePerMinute(currencyIncomePerMinuteBefore + item.getCurrencyIncomeIncreaseInMinute());
        userSave.setCurrencyIncomePerClick(currencyIncomePerClickBefore + item.getCurrencyIncomeIncreasePerClick());

        ItemPurchasedEvent itemPurchasedEvent = ItemPurchasedEvent.builder()
                .userSave(userSave)
                .item(item)
                .build();

        List<AchievementState> achievementStates = userEventsAchievementsProcessor.processItemPurchasedEvent(itemPurchasedEvent);
        userSave.getCompletedAchievementsIds().addAll(AchievementStatesUtil.getCompletedAchievementIds(achievementStates));
        UserSaveDocument savedDocument = userSaveService.saveDocument(userSave);

        return PurchaseItemResult.builder()
                .achievementStates(achievementStates)
                .currencyPayedForItemPurchase(item.getPrice())
                .currencyIncomePerMinuteBefore(currencyIncomePerMinuteBefore)
                .currencyIncomePerClickBefore(currencyIncomePerClickBefore)
                .currencyIncomePerClickAfter(savedDocument.getCurrencyIncomePerClick())
                .currencyIncomePerMinuteAfter(savedDocument.getCurrencyIncomePerMinute())
                .build();
    }
}
