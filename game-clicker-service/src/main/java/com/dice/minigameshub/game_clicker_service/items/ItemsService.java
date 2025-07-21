package com.dice.minigameshub.game_clicker_service.items;

import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import com.dice.minigameshub.game_clicker_service.items.dto.CreateItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.UpdateItemInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ItemsService {

    private final ItemsRepository itemsRepository;

    public ItemDocument getItemById(String itemId) {
        return itemsRepository.findById(itemId)
                .orElseThrow(() -> new ServiceException(String.format("Item by id %s not found", itemId), Error.ITEM_NOT_FOUND));
    }

    public List<ItemDocument> getAllItems() {
        return itemsRepository.findAll();
    }

    public ItemDocument updateOrCreateItem(UpdateItemInput input) {
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

    public ItemDocument createItem(CreateItemInput input) {
        ItemDocument createdItem = ItemDocument.builder()
                .id(UUID.randomUUID().toString())
                .name(input.getName())
                .description(input.getDescription())
                .price(input.getPrice())
                .currencyIncomeIncreaseInMinute(input.getCurrencyIncomeIncreaseInMinute())
                .currencyIncomeIncreasePerClick(input.getCurrencyIncomeIncreasePerClick())
                .build();

        return itemsRepository.save(createdItem);
    }
}
