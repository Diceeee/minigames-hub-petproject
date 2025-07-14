package com.dice.minigameshub.game_clicker_service.core.api;

import com.dice.minigameshub.game_clicker_service.core.CoreGameMapper;
import com.dice.minigameshub.game_clicker_service.core.CoreGameService;
import com.dice.minigameshub.game_clicker_service.core.api.dto.clicks.ClicksRequest;
import com.dice.minigameshub.game_clicker_service.core.api.dto.clicks.ClicksResponse;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessInput;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameInput;
import com.dice.minigameshub.game_clicker_service.items.api.dto.PurchaseItemResponse;
import com.dice.minigameshub.game_clicker_service.core.api.dto.start.StartGameResponse;
import com.dice.minigameshub.game_clicker_service.items.purchase.PurchaseItemMapper;
import com.dice.minigameshub.game_clicker_service.common.Headers;
import com.dice.minigameshub.game_clicker_service.common.UserDetails;
import com.dice.minigameshub.game_clicker_service.items.purchase.PurchaseItemInput;
import com.dice.minigameshub.game_clicker_service.items.purchase.PurchaseItemResult;
import com.dice.minigameshub.game_clicker_service.items.purchase.PurchaseItemsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/public")
public class GameClickerCoreUserApi {

    private final CoreGameService coreGameService;
    private final CoreGameMapper coreGameMapper;

    private final PurchaseItemsService purchaseItemsService;
    private final PurchaseItemMapper purchaseItemMapper;

    @PostMapping("/clicks")
    public ClicksResponse clicks(@RequestBody ClicksRequest clicksRequest,
                                 @RequestHeader(Headers.USER_ID) Long userId,
                                 @RequestHeader(Headers.SESSION_ID) String sessionId) {

        UserDetails userDetails = UserDetails.of(userId, sessionId);
        return coreGameMapper.mapToResponse(coreGameService.processClicks(new ClicksProcessInput(clicksRequest.getClicks(), userDetails)));
    }

    @PostMapping("/start")
    public StartGameResponse startGame(@RequestHeader(Headers.USER_ID) Long userId,
                                       @RequestHeader(Headers.SESSION_ID) String sessionId) {

        UserDetails userDetails = UserDetails.of(userId, sessionId);
        return coreGameMapper.mapToResponse(coreGameService.startGame(new StartGameInput(userDetails)));
    }

    @PostMapping("/purchase/{itemId}")
    public PurchaseItemResponse itemPurchase(@PathVariable String itemId,
                                             @RequestHeader(Headers.USER_ID) Long userId,
                                             @RequestHeader(Headers.SESSION_ID) String sessionId) {

        UserDetails userDetails = UserDetails.of(userId, sessionId);

        PurchaseItemResult purchaseItemResult = purchaseItemsService.purchaseItem(PurchaseItemInput.builder()
                .userDetails(userDetails)
                .itemId(itemId)
                .build());

        return purchaseItemMapper.mapPurchaseItemResponse(purchaseItemResult);
    }
}
