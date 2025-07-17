package com.dice.minigameshub.game_clicker_service.core.dto.start;

import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class StartGameResult {
    int basicCurrencyGainPerClick;
    long currency;
    long currencyIncomePerClick;
    long currencyIncomePerMinute;

    boolean firstStart;

    UserStatistics userStatistics;
    List<String> purchasedItemsIds;

    public List<String> getPurchasedItemsIds() {
        return Collections.unmodifiableList(purchasedItemsIds);
    }
}
