package com.dice.minigameshub.game_clicker_service.achievement.util;

import com.dice.minigameshub.game_clicker_service.achievement.domain.AchievementState;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class AchievementStatesUtil {

    public Set<String> getCompletedAchievementIds(Collection<AchievementState> states) {
        return states.stream()
                .filter(AchievementState::isCompleted)
                .map(AchievementState::getAchievementId)
                .collect(Collectors.toSet());
    }
}
