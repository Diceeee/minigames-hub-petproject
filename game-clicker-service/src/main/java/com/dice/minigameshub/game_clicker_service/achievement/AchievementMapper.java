package com.dice.minigameshub.game_clicker_service.achievement;

import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementResponse;
import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementStateResponse;
import com.dice.minigameshub.game_clicker_service.achievement.api.dto.ProgressResponse;
import com.dice.minigameshub.game_clicker_service.achievement.dto.AchievementState;
import com.dice.minigameshub.game_clicker_service.achievement.dto.Progress;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public interface AchievementMapper {

    AchievementResponse mapToResponse(AchievementDocument document);
    AchievementStateResponse mapToResponse(AchievementState dto);
    ProgressResponse mapToResponse(Progress dto);
}
