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
        UserSaveDocument userSaveDocument = userSaveService.getUserSave(clicksInput.userDetails().getUserId());
        GameConfig gameConfig = gameConfigService.getGameConfig();

        long currencyEarned = (long) gameConfig.getBasicCurrencyGainPerClick() * clicksInput.clicks();
        UserSaveDocument processedUserSaveDocument = userSaveDocument.toBuilder()
                .currency(userSaveDocument.getCurrency() + currencyEarned)
                .build();

        userSaveService.saveDocument(processedUserSaveDocument);
        return new ClicksProcessResult(userSaveDocument.getCurrency(), processedUserSaveDocument.getCurrency());
    }

    public StartGameResult startGame(StartGameInput startGameInput) {
        GameConfig gameConfig = gameConfigService.getGameConfig();

        if (userSaveService.isUserGameSaveExists(startGameInput.userDetails().getUserId())) {
            UserSaveDocument userSaveDocument = userSaveService.getUserSave(startGameInput.userDetails().getUserId());

            return new StartGameResult(
                    coreGameMapper.mapDocument(userSaveDocument.getUserStatisticsDocument()),
                    userSaveDocument.getPurchasedItemsIds(),
                    gameConfig.getBasicCurrencyGainPerClick(),
                    userSaveDocument.getCurrency(),
                    false
            );
        }

        UserSaveDocument createdSaveDocument = UserSaveDocument.builder()
                .id(startGameInput.userDetails().getUserId())
                .purchasedItemsIds(Collections.emptyList())
                .currency(0L)
                .userStatisticsDocument(UserStatisticsDocument.builder()
                        .totalClicks(0L)
                        .totalCurrencyEarned(0L)
                        .totalCurrencyWasted(0L)
                        .build())
                .build();

        UserSaveDocument savedDocument = userSaveService.saveDocument(createdSaveDocument);
        return new StartGameResult(
                coreGameMapper.mapDocument(savedDocument.getUserStatisticsDocument()),
                savedDocument.getPurchasedItemsIds(),
                gameConfig.getBasicCurrencyGainPerClick(),
                savedDocument.getCurrency(),
                true
        );
    }
}
