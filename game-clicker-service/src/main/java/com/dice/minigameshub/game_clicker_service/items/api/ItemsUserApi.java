package com.dice.minigameshub.game_clicker_service.items.api;


import com.dice.minigameshub.game_clicker_service.common.Headers;
import com.dice.minigameshub.game_clicker_service.common.UserDetails;
import com.dice.minigameshub.game_clicker_service.items.ItemsMapper;
import com.dice.minigameshub.game_clicker_service.items.ItemsService;
import com.dice.minigameshub.game_clicker_service.items.api.dto.ItemResponse;
import com.dice.minigameshub.game_clicker_service.items.api.dto.PurchaseItemResponse;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemInput;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/public/items")
public class ItemsUserApi {

    private final ItemsService itemsService;
    private final ItemsMapper itemsMapper;

    @PostMapping("/purchase/{itemId}")
    public PurchaseItemResponse itemPurchase(@PathVariable String itemId,
                                             @RequestHeader(Headers.USER_ID) Long userId,
                                             @RequestHeader(Headers.SESSION_ID) String sessionId) {

        UserDetails userDetails = UserDetails.of(userId, sessionId);

        PurchaseItemResult purchaseItemResult = itemsService.purchaseItem(PurchaseItemInput.builder()
                .userDetails(userDetails)
                .itemId(itemId)
                .build());

        return itemsMapper.mapToResponse(purchaseItemResult);
    }

    @GetMapping
    public List<ItemResponse> getItems() {
        return itemsService.getAllItems().stream()
                .map(itemsMapper::mapToResponse)
                .toList();
    }
}
