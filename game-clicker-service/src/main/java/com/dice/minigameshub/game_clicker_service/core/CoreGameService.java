package com.dice.minigameshub.game_clicker_service.core;

import com.dice.minigameshub.game_clicker_service.achievement.UserEventsAchievementsProcessor;
import com.dice.minigameshub.game_clicker_service.achievement.domain.AchievementState;
import com.dice.minigameshub.game_clicker_service.achievement.util.AchievementStatesUtil;
import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import com.dice.minigameshub.game_clicker_service.common.util.CollectionUtils;
import com.dice.minigameshub.game_clicker_service.core.config.CheatProperties;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessInput;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessResult;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameInput;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameResult;
import com.dice.minigameshub.game_clicker_service.core.event.UserClicksProcessedEvent;
import com.dice.minigameshub.game_clicker_service.core.event.UserStartedGameEvent;
import com.dice.minigameshub.game_clicker_service.gameconfig.GameConfig;
import com.dice.minigameshub.game_clicker_service.gameconfig.GameConfigService;
import com.dice.minigameshub.game_clicker_service.save.UserSaveService;
import com.dice.minigameshub.game_clicker_service.save.document.ClicksEventDocument;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import com.dice.minigameshub.game_clicker_service.save.document.UserStatisticsDocument;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CoreGameService {

    private final UserEventsAchievementsProcessor userEventsAchievementsProcessor;
    private final SameGameSessionValidator sameGameSessionValidator;
    private final UserSaveService userSaveService;
    private final GameConfigService gameConfigService;
    private final CheatProperties cheatProperties;
    private final CoreGameMapper coreGameMapper;
    private final Clock clock;

    public ClicksProcessResult processClicks(ClicksProcessInput clicksInput) {
        Instant now = clock.instant();
        UserSaveDocument userSaveDocument = userSaveService.getUserSave(clicksInput.getUserDetails().getUserId());
        sameGameSessionValidator.validateSameGameSession(userSaveDocument, clicksInput.getUserDetails());

        long currencyBefore = userSaveDocument.getCurrency();
        long currencyEarned = (long) userSaveDocument.getCurrencyIncomePerClick() * clicksInput.getClicks();

        List<ClicksEventDocument> clickEventsInAllowedClicksTimeWindow = CollectionUtils.combineLists(
                getClicksInAllowedClicksTimeWindow(userSaveDocument.getUserStatistics().getClickEvents(), now),
                List.of(ClicksEventDocument.builder().clicks(clicksInput.getClicks()).timestamp(now).build()));
        validateClicksCheating(clickEventsInAllowedClicksTimeWindow, clicksInput);

        UserSaveDocument currencyAndStatisticsUpdatedUserSave = userSaveDocument.toBuilder()
                .currency(currencyBefore + currencyEarned)
                .userStatistics(userSaveDocument.getUserStatistics().toBuilder()
                        .totalClicks(userSaveDocument.getUserStatistics().getTotalClicks() + clicksInput.getClicks())
                        .totalCurrencyEarned(userSaveDocument.getUserStatistics().getTotalCurrencyEarned() + currencyEarned)
                        .clickEvents(clickEventsInAllowedClicksTimeWindow)
                        .build())
                .build();

        UserClicksProcessedEvent userClicksProcessedEvent = UserClicksProcessedEvent.builder()
                .userSave(currencyAndStatisticsUpdatedUserSave)
                .clicks(clicksInput.getClicks())
                .currencyEarned(currencyEarned)
                .build();

        List<AchievementState> achievementStates = userEventsAchievementsProcessor.processClicksProcessedEvent(userClicksProcessedEvent);
        UserSaveDocument userSaveDocumentWithUpdatedAchievements = currencyAndStatisticsUpdatedUserSave
                .withCompletedAchievementsIds(CollectionUtils.combineSets(
                        currencyAndStatisticsUpdatedUserSave.getCompletedAchievementsIds(),
                        AchievementStatesUtil.getCompletedAchievementIds(achievementStates)));

        UserSaveDocument savedDocument = userSaveService.saveDocument(userSaveDocumentWithUpdatedAchievements);
        return ClicksProcessResult.builder()
                .currencyBeforeClicks(currencyBefore)
                .currencyAfterClicks(savedDocument.getCurrency())
                .userStatistics(coreGameMapper.mapDocument(savedDocument.getUserStatistics()))
                .achievementStates(achievementStates)
                .build();
    }

    public StartGameResult startGame(StartGameInput startGameInput) {
        GameConfig gameConfig = gameConfigService.getGameConfig();

        if (userSaveService.isUserGameSaveExists(startGameInput.getUserDetails().getUserId())) {
            UserSaveDocument userSaveDocument = userSaveService.getUserSave(startGameInput.getUserDetails().getUserId())
                    .withLastSessionId(startGameInput.getUserDetails().getSessionId());

            UserSaveDocument savedDocument = userSaveService.saveDocument(userSaveDocument);
            UserStartedGameEvent userStartedGameEvent = UserStartedGameEvent.builder()
                    .userSave(savedDocument)
                    .build();

            List<AchievementState> achievementStates = userEventsAchievementsProcessor.processStartedGameEvent(userStartedGameEvent);
            return StartGameResult.builder()
                    .currency(savedDocument.getCurrency())
                    .currencyIncomePerClick(savedDocument.getCurrencyIncomePerClick())
                    .currencyIncomePerMinute(savedDocument.getCurrencyIncomePerMinute())
                    .userStatistics(coreGameMapper.mapDocument(savedDocument.getUserStatistics()))
                    .purchasedItemsIds(savedDocument.getPurchasedItemsIds())
                    .achievementStates(achievementStates)
                    .firstStart(false)
                    .build();
        }

        UserSaveDocument createdSaveDocument = UserSaveDocument.builder()
                .id(startGameInput.getUserDetails().getUserId())
                .currencyIncomePerClick(gameConfig.getBasicCurrencyGainPerClick())
                .userStatistics(UserStatisticsDocument.createNew())
                .lastSessionId(startGameInput.getUserDetails().getSessionId())
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

    private void validateClicksCheating(List<ClicksEventDocument> clickEventsInAllowedClicksTimeWindow, ClicksProcessInput clicksInput) {
        int clicksInAllowedClicksTimeWindow = calculateClicksCount(clickEventsInAllowedClicksTimeWindow);
        if (clicksInAllowedClicksTimeWindow >= cheatProperties.getAllowedClicksPerTimeWindow()) {
            log.info("User '{}' exceed clicks limit '{}' in '{}' seconds", clicksInput.getUserDetails(),
                    cheatProperties.getAllowedClicksPerTimeWindow(), cheatProperties.getAllowedClicksTimeWindowInSeconds());
            throw new ServiceException(String.format("Clicks limit exceed for now, please, wait up to %d seconds to continue",
                    cheatProperties.getAllowedClicksTimeWindowInSeconds()), Error.CLICKS_LIMIT_PER_MINUTE_EXCEED);
        }
    }

    private List<ClicksEventDocument> getClicksInAllowedClicksTimeWindow(List<ClicksEventDocument> clickEvents, Instant now) {
        return clickEvents.stream()
                .filter(event -> Duration.between(event.getTimestamp(), now).toSeconds() < cheatProperties.getAllowedClicksTimeWindowInSeconds())
                .toList();
    }

    private int calculateClicksCount(List<ClicksEventDocument> clickEvents) {
        return clickEvents.stream().mapToInt(ClicksEventDocument::getClicks).sum();
    }
}
