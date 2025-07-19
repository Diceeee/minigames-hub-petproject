package com.dice.minigameshub.game_clicker_service.core.dto.start;

import com.dice.minigameshub.game_clicker_service.achievement.dto.AchievementState;
import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Value
@Builder
public class StartGameResult {
    long currency;
    long currencyIncomePerClick;
    long currencyIncomePerMinute;

    boolean firstStart;

    UserStatistics userStatistics;
    Set<String> purchasedItemsIds;
    List<AchievementState> achievementStates;

    public Set<String> getPurchasedItemsIds() {
        return Collections.unmodifiableSet(purchasedItemsIds);
    }

    public List<AchievementState> getAchievementStates() {
        return Collections.unmodifiableList(achievementStates);
    }
}
