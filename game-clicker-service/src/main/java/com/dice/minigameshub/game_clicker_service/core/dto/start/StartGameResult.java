package com.dice.minigameshub.game_clicker_service.core.dto.start;

import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;

import java.util.List;

public record StartGameResult(UserStatistics userStatistics,
                              List<String> purchasedItemsIds,
                              Integer currencyGainPerClick,
                              Long currency,
                              boolean firstStart) {

}
