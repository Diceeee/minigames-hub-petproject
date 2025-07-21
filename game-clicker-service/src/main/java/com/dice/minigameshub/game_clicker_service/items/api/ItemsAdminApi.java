package com.dice.minigameshub.game_clicker_service.items.api;

import com.dice.minigameshub.game_clicker_service.items.ItemDocument;
import com.dice.minigameshub.game_clicker_service.items.ItemsMapper;
import com.dice.minigameshub.game_clicker_service.items.ItemsService;
import com.dice.minigameshub.game_clicker_service.items.api.dto.ItemResponse;
import com.dice.minigameshub.game_clicker_service.items.api.dto.ItemRequest;
import com.dice.minigameshub.game_clicker_service.items.dto.CreateItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.UpdateItemInput;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/items")
public class ItemsAdminApi {

    private final ItemsService itemsService;
    private final ItemsMapper itemsMapper;

    @PutMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrCreateItem(@PathVariable String itemId, @RequestBody @Valid ItemRequest request) {
        ItemDocument itemDocument = itemsService.updateOrCreateItem(UpdateItemInput.builder()
                .itemId(itemId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .currencyIncomeIncreaseInMinute(request.getCurrencyIncomeIncreaseInMinute())
                .currencyIncomeIncreasePerClick(request.getCurrencyIncomeIncreasePerClick())
                .build());

        log.info("Item successfully put: {}", itemDocument);
    }

    @PostMapping
    public ItemResponse createItem(@RequestBody @Valid ItemRequest request) {
        ItemDocument itemDocument = itemsService.createItem(CreateItemInput.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .currencyIncomeIncreaseInMinute(request.getCurrencyIncomeIncreaseInMinute())
                .currencyIncomeIncreasePerClick(request.getCurrencyIncomeIncreasePerClick())
                .build());

        log.info("Item successfully created: {}", itemDocument);
        return itemsMapper.mapToResponse(itemDocument);
    }
}
