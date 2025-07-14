package com.dice.minigameshub.game_clicker_service.gameconfig;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public interface GameConfigMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = "id")
    GameConfig mapDocument(GameConfigDocument document);
}
