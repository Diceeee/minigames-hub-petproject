package com.dice.minigameshub.game_clicker_service.core.api.dto.start;

import com.dice.minigameshub.game_clicker_service.core.api.dto.UserStatisticsResponse;

import java.util.List;

public record StartGameResponse(UserStatisticsResponse userStatistics,
                                List<String> purchasedItemsIds,
                                Integer currencyGainPerClick,
                                Long currency,
                                boolean firstStart) {

}
