package com.dice.minigameshub.game_clicker_service.items;

import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import com.dice.minigameshub.game_clicker_service.items.dto.UpdateOrCreateItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemResult;
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

    public PurchaseItemResult purchaseItem(PurchaseItemInput input) {
        UserSaveDocument userSave = userSaveService.getUserSave(input.getUserDetails().getUserId());
        if (userSave.getPurchasedItemsIds().contains(input.getItemId())) {
            throw new ServiceException(String.format("Item %s is already purchased by user %d", input.getItemId(), input.getUserDetails().getUserId()),
                    Error.ITEM_ALREADY_PURCHASED);
        }

        ItemDocument item = getItemById(input.getItemId());
        if (item.getPrice() > userSave.getCurrency()) {
            throw new ServiceException(String.format("User %d has only %d currency while item %s price is %d",
                    input.getUserDetails().getUserId(), userSave.getCurrency(), input.getItemId(), item.getPrice()), Error.NOT_ENOUGH_CURRENCY);
        }

        userSave.setCurrency(userSave.getCurrency() - item.getPrice());

        int currencyIncomePerClickBefore = userSave.getCurrencyIncomePerClick();
        int currencyIncomePerMinuteBefore = userSave.getCurrencyIncomePerMinute();
        userSave.setCurrencyIncomePerMinute(currencyIncomePerMinuteBefore + item.getCurrencyIncomeIncreaseInMinute());
        userSave.setCurrencyIncomePerClick(currencyIncomePerClickBefore + item.getCurrencyIncomeIncreasePerClick());

        UserSaveDocument savedDocument = userSaveService.saveDocument(userSave);

        return PurchaseItemResult.builder()
                .currencyPayedForItemPurchase(item.getPrice())
                .currencyIncomePerMinuteBefore(currencyIncomePerMinuteBefore)
                .currencyIncomePerClickBefore(currencyIncomePerClickBefore)
                .currencyIncomePerClickAfter(savedDocument.getCurrencyIncomePerClick())
                .currencyIncomePerMinuteAfter(savedDocument.getCurrencyIncomePerMinute())
                .build();
    }

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
