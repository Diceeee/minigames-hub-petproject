package com.dice.minigameshub.game_clicker_service.achievement;

import com.dice.minigameshub.game_clicker_service.achievement.domain.AchievementState;
import com.dice.minigameshub.game_clicker_service.achievement.domain.Progress;
import com.dice.minigameshub.game_clicker_service.achievement.target.CurrencyTarget;
import com.dice.minigameshub.game_clicker_service.achievement.target.CurrencyTargetType;
import com.dice.minigameshub.game_clicker_service.achievement.target.ItemTarget;
import com.dice.minigameshub.game_clicker_service.achievement.target.ItemTargetType;
import com.dice.minigameshub.game_clicker_service.core.event.UserClicksProcessedEvent;
import com.dice.minigameshub.game_clicker_service.core.event.UserStartedGameEvent;
import com.dice.minigameshub.game_clicker_service.items.event.ItemPurchasedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class UserEventsAchievementsProcessor {

    private final AchievementService achievementService;

    public List<AchievementState> processStartedGameEvent(UserStartedGameEvent event) {
        List<AchievementDocument> allAchievements = achievementService.getAllAchievements();
        List<AchievementState> achievementStates = new ArrayList<>();

        for (AchievementDocument achievement : allAchievements) {
            if (achievement.getTarget() instanceof ItemTarget itemBoughtTarget && itemBoughtTarget.getItemTargetType() == ItemTargetType.BOUGHT) {
                achievementStates.add(AchievementState.builder()
                        .achievementId(achievement.getId())
                        .completed(event.getUserSave().getPurchasedItemsIds().contains(itemBoughtTarget.getItemId()))
                        .build());
            }

            Long current = null;
            if (achievement.getTarget() instanceof CurrencyTarget currencyTarget) {
                switch (currencyTarget.getCurrencyTargetType()) {
                    case BALANCE -> current = event.getUserSave().getCurrency();
                    case SPENT -> current = event.getUserSave().getUserStatistics().getTotalCurrencySpent();
                    case CURRENCY_IN_MINUTE_HIGHER_THAN ->
                            current = (long) event.getUserSave().getCurrencyIncomePerMinute();
                    case CURRENCY_PER_CLICK_HIGHER_THAN ->
                            current = (long) event.getUserSave().getCurrencyIncomePerClick();
                }

                if (current != null) {
                    Progress progress = Progress.builder()
                            .current(current)
                            .target(currencyTarget.getAmount())
                            .build();

                    achievementStates.add(
                            AchievementState.builder()
                                    .achievementId(achievement.getId())
                                    .completed(progress.isCompleted())
                                    .progress(progress)
                                    .build());
                }
            }
        }

        return achievementStates;
    }

    public List<AchievementState> processClicksProcessedEvent(UserClicksProcessedEvent event) {
        List<AchievementDocument> notCompletedAchievements = achievementService.getAllAchievementsExcept(event.getUserSave().getCompletedAchievementsIds());
        List<AchievementState> changedAchievements = new ArrayList<>();

        for (AchievementDocument achievement : notCompletedAchievements) {
            if (achievement.getTarget() instanceof CurrencyTarget currencyTarget && currencyTarget.getCurrencyTargetType() == CurrencyTargetType.BALANCE) {
                Progress progress = Progress.builder()
                        .current(event.getUserSave().getCurrency())
                        .target(currencyTarget.getAmount())
                        .delta(event.getCurrencyEarned())
                        .build();

                changedAchievements.add(
                        AchievementState.builder()
                                .achievementId(achievement.getId())
                                .completed(progress.isCompleted())
                                .progress(progress)
                                .build());
            }
        }

        return changedAchievements;
    }

    public List<AchievementState> processItemPurchasedEvent(ItemPurchasedEvent event) {
        List<AchievementDocument> notCompletedAchievements = achievementService.getAllAchievementsExcept(event.getUserSave().getCompletedAchievementsIds());
        List<AchievementState> changedAchievements = new ArrayList<>();
        for (AchievementDocument achievement : notCompletedAchievements) {
            if (achievement.getTarget() instanceof ItemTarget itemBoughtTarget && itemBoughtTarget.getItemTargetType() == ItemTargetType.BOUGHT) {
                changedAchievements.add(AchievementState.builder()
                        .achievementId(achievement.getId())
                        .completed(true)
                        .build());
            }

            Long current = null;
            if (achievement.getTarget() instanceof CurrencyTarget currencyTarget) {
                switch (currencyTarget.getCurrencyTargetType()) {
                    case SPENT -> current = event.getUserSave().getUserStatistics().getTotalCurrencySpent();
                    case CURRENCY_IN_MINUTE_HIGHER_THAN ->
                            current = (long) event.getUserSave().getCurrencyIncomePerMinute();
                    case CURRENCY_PER_CLICK_HIGHER_THAN ->
                            current = (long) event.getUserSave().getCurrencyIncomePerClick();
                }

                if (current != null) {
                    Progress progress = Progress.builder()
                            .current(current)
                            .target(currencyTarget.getAmount())
                            .delta(event.getItem().getPrice())
                            .build();

                    changedAchievements.add(
                            AchievementState.builder()
                                    .achievementId(achievement.getId())
                                    .completed(progress.isCompleted())
                                    .progress(progress)
                                    .build());
                }
            }
        }

        return changedAchievements;
    }
}
