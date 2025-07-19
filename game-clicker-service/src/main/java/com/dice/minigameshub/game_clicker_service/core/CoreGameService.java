package com.dice.minigameshub.game_clicker_service.core;

import com.dice.minigameshub.game_clicker_service.achievement.UserEventsAchievementsProcessor;
import com.dice.minigameshub.game_clicker_service.achievement.dto.AchievementState;
import com.dice.minigameshub.game_clicker_service.achievement.util.AchievementStatesUtil;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessInput;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessResult;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameInput;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameResult;
import com.dice.minigameshub.game_clicker_service.core.event.UserClicksProcessedEvent;
import com.dice.minigameshub.game_clicker_service.core.event.UserStartedGameEvent;
import com.dice.minigameshub.game_clicker_service.gameconfig.GameConfig;
import com.dice.minigameshub.game_clicker_service.gameconfig.GameConfigService;
import com.dice.minigameshub.game_clicker_service.save.UserSaveService;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import com.dice.minigameshub.game_clicker_service.save.document.UserStatisticsDocument;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CoreGameService {

    private final UserEventsAchievementsProcessor userEventsAchievementsProcessor;
    private final UserSaveService userSaveService;
    private final GameConfigService gameConfigService;
    private final CoreGameMapper coreGameMapper;

    public ClicksProcessResult processClicks(ClicksProcessInput clicksInput) {
        UserSaveDocument userSaveDocument = userSaveService.getUserSave(clicksInput.getUserDetails().getUserId());
        GameConfig gameConfig = gameConfigService.getGameConfig();

        long currencyBefore = userSaveDocument.getCurrency();
        long currencyEarned = (long) gameConfig.getBasicCurrencyGainPerClick() * clicksInput.getClicks();

        userSaveDocument.setCurrency(currencyBefore + currencyEarned);
        UserClicksProcessedEvent userClicksProcessedEvent = UserClicksProcessedEvent.builder()
                .userSave(userSaveDocument)
                .clicks(clicksInput.getClicks())
                .currencyEarned(currencyEarned)
                .build();

        List<AchievementState> achievementStates = userEventsAchievementsProcessor.processClicksProcessedEvent(userClicksProcessedEvent);
        userSaveDocument.getCompletedAchievementsIds().addAll(AchievementStatesUtil.getCompletedAchievementIds(achievementStates));

        userSaveService.saveDocument(userSaveDocument);
        return ClicksProcessResult.builder()
                .currencyBeforeClicks(currencyBefore)
                .currencyAfterClicks(userSaveDocument.getCurrency())
                .achievementStates(achievementStates)
                .build();
    }

    public StartGameResult startGame(StartGameInput startGameInput) {
        GameConfig gameConfig = gameConfigService.getGameConfig();

        if (userSaveService.isUserGameSaveExists(startGameInput.getUserDetails().getUserId())) {
            UserSaveDocument userSaveDocument = userSaveService.getUserSave(startGameInput.getUserDetails().getUserId());
            UserStartedGameEvent userStartedGameEvent = UserStartedGameEvent.builder()
                    .userSave(userSaveDocument)
                    .build();

            List<AchievementState> achievementStates = userEventsAchievementsProcessor.processStartedGameEvent(userStartedGameEvent);
            return StartGameResult.builder()
                    .currency(userSaveDocument.getCurrency())
                    .currencyIncomePerClick(userSaveDocument.getCurrencyIncomePerClick())
                    .currencyIncomePerMinute(userSaveDocument.getCurrencyIncomePerMinute())
                    .userStatistics(coreGameMapper.mapDocument(userSaveDocument.getUserStatistics()))
                    .purchasedItemsIds(userSaveDocument.getPurchasedItemsIds())
                    .achievementStates(achievementStates)
                    .firstStart(false)
                    .build();
        }

        UserSaveDocument createdSaveDocument = UserSaveDocument.builder()
                .id(startGameInput.getUserDetails().getUserId())
                .currencyIncomePerClick(gameConfig.getBasicCurrencyGainPerClick())
                .purchasedItemsIds(Collections.emptySet())
                .userStatistics(UserStatisticsDocument.builder().build())
                .build();

        UserSaveDocument savedDocument = userSaveService.saveDocument(createdSaveDocument);
        UserStartedGameEvent userStartedGameEvent = UserStartedGameEvent.builder()
                .userSave(createdSaveDocument)
                .build();

        List<AchievementState> achievementStates = userEventsAchievementsProcessor.processStartedGameEvent(userStartedGameEvent);
        return StartGameResult.builder()
                .currency(savedDocument.getCurrency())
                .currencyIncomePerClick(savedDocument.getCurrencyIncomePerClick())
                .currencyIncomePerMinute(savedDocument.getCurrencyIncomePerMinute())
                .userStatistics(coreGameMapper.mapDocument(savedDocument.getUserStatistics()))
                .purchasedItemsIds(savedDocument.getPurchasedItemsIds())
                .achievementStates(achievementStates)
                .firstStart(true)
                .build();
    }
}
