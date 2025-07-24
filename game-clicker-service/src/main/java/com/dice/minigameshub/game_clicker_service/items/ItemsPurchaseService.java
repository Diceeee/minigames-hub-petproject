package com.dice.minigameshub.game_clicker_service.items;

import com.dice.minigameshub.game_clicker_service.achievement.UserEventsAchievementsProcessor;
import com.dice.minigameshub.game_clicker_service.achievement.domain.AchievementState;
import com.dice.minigameshub.game_clicker_service.achievement.util.AchievementStatesUtil;
import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import com.dice.minigameshub.game_clicker_service.common.util.CollectionUtils;
import com.dice.minigameshub.game_clicker_service.core.CoreGameMapper;
import com.dice.minigameshub.game_clicker_service.core.SameGameSessionValidator;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemResult;
import com.dice.minigameshub.game_clicker_service.items.event.ItemPurchasedEvent;
import com.dice.minigameshub.game_clicker_service.save.UserSaveService;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ItemsPurchaseService {

    private final UserEventsAchievementsProcessor userEventsAchievementsProcessor;
    private final SameGameSessionValidator sameGameSessionValidator;
    private final UserSaveService userSaveService;
    private final ItemsService itemsService;
    private final CoreGameMapper coreGameMapper;

    public PurchaseItemResult purchaseItem(PurchaseItemInput input) {
        UserSaveDocument userSaveBeforePurchase = userSaveService.getUserSave(input.getUserDetails().getUserId());
        sameGameSessionValidator.validateSameGameSession(userSaveBeforePurchase, input.getUserDetails());

        if (userSaveBeforePurchase.getPurchasedItemsIds().contains(input.getItemId())) {
            throw new ServiceException(String.format("Item %s is already purchased by user %d", input.getItemId(), input.getUserDetails().getUserId()),
                    Error.ITEM_ALREADY_PURCHASED);
        }

        ItemDocument item = itemsService.getItemById(input.getItemId());
        if (item.getPrice() > userSaveBeforePurchase.getCurrency()) {
            throw new ServiceException(String.format("User %d has only %d currency while item %s price is %d",
                    input.getUserDetails().getUserId(), userSaveBeforePurchase.getCurrency(), input.getItemId(), item.getPrice()), Error.NOT_ENOUGH_CURRENCY);
        }

        Set<String> purchasedItemsIds = new HashSet<>(userSaveBeforePurchase.getPurchasedItemsIds());
        purchasedItemsIds.add(input.getItemId());

        UserSaveDocument userSaveAfterPurchase = userSaveBeforePurchase.toBuilder()
                .purchasedItemsIds(purchasedItemsIds)
                .currency(userSaveBeforePurchase.getCurrency() - item.getPrice())
                .currencyIncomePerMinute(userSaveBeforePurchase.getCurrencyIncomePerMinute() + item.getCurrencyIncomeIncreaseInMinute())
                .currencyIncomePerClick(userSaveBeforePurchase.getCurrencyIncomePerClick() + item.getCurrencyIncomeIncreasePerClick())
                .userStatistics(userSaveBeforePurchase.getUserStatistics().toBuilder()
                        .totalCurrencySpent(userSaveBeforePurchase.getUserStatistics().getTotalCurrencySpent() + item.getPrice())
                        .build())
                .build();

        ItemPurchasedEvent itemPurchasedEvent = ItemPurchasedEvent.builder()
                .userSave(userSaveAfterPurchase)
                .item(item)
                .build();

        List<AchievementState> achievementStates = userEventsAchievementsProcessor.processItemPurchasedEvent(itemPurchasedEvent);
        UserSaveDocument userSaveDocumentWithUpdatedAchievements = userSaveAfterPurchase
                .withCompletedAchievementsIds(CollectionUtils.combineSets(
                        userSaveAfterPurchase.getCompletedAchievementsIds(),
                        AchievementStatesUtil.getCompletedAchievementIds(achievementStates)));

        UserSaveDocument savedDocument = userSaveService.saveDocument(userSaveDocumentWithUpdatedAchievements);

        return PurchaseItemResult.builder()
                .itemId(item.getId())
                .achievementStates(achievementStates)
                .currencyPayedForItemPurchase(item.getPrice())
                .currencyIncomePerMinuteBefore(userSaveBeforePurchase.getCurrencyIncomePerMinute())
                .currencyIncomePerClickBefore(userSaveBeforePurchase.getCurrencyIncomePerClick())
                .currencyIncomePerClickAfter(savedDocument.getCurrencyIncomePerClick())
                .currencyIncomePerMinuteAfter(savedDocument.getCurrencyIncomePerMinute())
                .userStatistics(coreGameMapper.mapDocument(savedDocument.getUserStatistics()))
                .build();
    }
}
