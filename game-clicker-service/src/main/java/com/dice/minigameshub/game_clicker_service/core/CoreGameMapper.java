package com.dice.minigameshub.game_clicker_service.core;

import com.dice.minigameshub.game_clicker_service.achievement.AchievementMapper;
import com.dice.minigameshub.game_clicker_service.core.api.dto.UserStatisticsResponse;
import com.dice.minigameshub.game_clicker_service.core.api.dto.clicks.ClicksResponse;
import com.dice.minigameshub.game_clicker_service.core.api.dto.start.StartGameResponse;
import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessResult;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameResult;
import com.dice.minigameshub.game_clicker_service.save.document.UserStatisticsDocument;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = AchievementMapper.class
)
public interface CoreGameMapper {

    UserStatistics mapDocument(UserStatisticsDocument document);

    UserStatisticsResponse mapToResponse(UserStatistics userStatistics);
    StartGameResponse mapToResponse(StartGameResult startGameResult);
    ClicksResponse mapToResponse(ClicksProcessResult clicksProcessResult);
}
