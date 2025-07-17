package com.dice.minigameshub.game_clicker_service.core;

import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessInput;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessResult;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameInput;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameResult;
import com.dice.minigameshub.game_clicker_service.gameconfig.GameConfig;
import com.dice.minigameshub.game_clicker_service.gameconfig.GameConfigService;
import com.dice.minigameshub.game_clicker_service.save.UserSaveService;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import com.dice.minigameshub.game_clicker_service.save.document.UserStatisticsDocument;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@AllArgsConstructor
public class CoreGameService {

    private final UserSaveService userSaveService;
    private final GameConfigService gameConfigService;
    private final CoreGameMapper coreGameMapper;

    public ClicksProcessResult processClicks(ClicksProcessInput clicksInput) {
        UserSaveDocument userSaveDocument = userSaveService.getUserSave(clicksInput.getUserDetails().getUserId());
        GameConfig gameConfig = gameConfigService.getGameConfig();

        long currencyBefore = userSaveDocument.getCurrency();
        long currencyEarned = (long) gameConfig.getBasicCurrencyGainPerClick() * clicksInput.getClicks();

        userSaveDocument.setCurrency(currencyBefore + currencyEarned);
        userSaveService.saveDocument(userSaveDocument);

        return ClicksProcessResult.builder()
                .currencyBeforeClicks(currencyBefore)
                .currencyAfterClicks(userSaveDocument.getCurrency())
                .build();
    }

    public StartGameResult startGame(StartGameInput startGameInput) {
        GameConfig gameConfig = gameConfigService.getGameConfig();

        if (userSaveService.isUserGameSaveExists(startGameInput.getUserDetails().getUserId())) {
            UserSaveDocument userSaveDocument = userSaveService.getUserSave(startGameInput.getUserDetails().getUserId());

            return StartGameResult.builder()
                    .basicCurrencyGainPerClick(gameConfig.getBasicCurrencyGainPerClick())
                    .currency(userSaveDocument.getCurrency())
                    .currencyIncomePerClick(userSaveDocument.getCurrencyIncomePerClick())
                    .currencyIncomePerMinute(userSaveDocument.getCurrencyIncomePerMinute())
                    .userStatistics(coreGameMapper.mapDocument(userSaveDocument.getUserStatistics()))
                    .purchasedItemsIds(userSaveDocument.getPurchasedItemsIds())
                    .firstStart(false)
                    .build();
        }

        UserSaveDocument createdSaveDocument = UserSaveDocument.builder()
                .id(startGameInput.getUserDetails().getUserId())
                .purchasedItemsIds(Collections.emptyList())
                .userStatistics(UserStatisticsDocument.builder().build())
                .build();

        UserSaveDocument savedDocument = userSaveService.saveDocument(createdSaveDocument);
        return StartGameResult.builder()
                .basicCurrencyGainPerClick(gameConfig.getBasicCurrencyGainPerClick())
                .currency(savedDocument.getCurrency())
                .currencyIncomePerClick(savedDocument.getCurrencyIncomePerClick())
                .currencyIncomePerMinute(savedDocument.getCurrencyIncomePerMinute())
                .userStatistics(coreGameMapper.mapDocument(savedDocument.getUserStatistics()))
                .purchasedItemsIds(savedDocument.getPurchasedItemsIds())
                .firstStart(true)
                .build();
    }
}
