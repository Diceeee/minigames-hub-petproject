package com.dice.minigameshub.game_clicker_service.achievement;

import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementResponse;
import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementStateResponse;
import com.dice.minigameshub.game_clicker_service.achievement.api.dto.ProgressResponse;
import com.dice.minigameshub.game_clicker_service.achievement.domain.AchievementState;
import com.dice.minigameshub.game_clicker_service.achievement.domain.Progress;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public interface AchievementMapper {

    AchievementResponse mapToResponse(AchievementDocument document);

    AchievementStateResponse mapToResponse(AchievementState state);

    @BeanMapping(ignoreUnmappedSourceProperties = "completed")
    ProgressResponse mapToResponse(Progress progress);
}
