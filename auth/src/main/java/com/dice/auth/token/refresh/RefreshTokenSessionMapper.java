package com.dice.auth.token.refresh;

import com.dice.auth.token.refresh.dto.RefreshTokenSession;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public interface RefreshTokenSessionMapper {

    RefreshTokenSession mapEntity(RefreshTokenSessionEntity entity);
    RefreshTokenSessionEntity mapToEntity(RefreshTokenSession dto);
}
