package com.dice.minigameshub.game_clicker_service.items.api;

import com.dice.minigameshub.game_clicker_service.items.ItemDocument;
import com.dice.minigameshub.game_clicker_service.items.ItemsService;
import com.dice.minigameshub.game_clicker_service.items.api.dto.UpdateOrCreateItemRequest;
import com.dice.minigameshub.game_clicker_service.items.dto.UpdateOrCreateItemInput;
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

    @PutMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrCreateItem(@PathVariable String itemId, @RequestBody @Valid UpdateOrCreateItemRequest request) {
        ItemDocument itemDocument = itemsService.updateOrCreateItem(UpdateOrCreateItemInput.builder()
                .itemId(itemId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .currencyIncomeIncreaseInMinute(request.getCurrencyIncomeIncreaseInMinute())
                .currencyIncomeIncreasePerClick(request.getCurrencyIncomeIncreasePerClick())
                .build());

        log.info("Item successfully put: {}", itemDocument);
    }
}
