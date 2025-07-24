package com.dice.minigameshub.game_clicker_service.save.document;

import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Value
@Builder(toBuilder = true)
public class UserStatisticsDocument {

    Long totalClicks;
    Long totalCurrencyEarned;
    Long totalCurrencySpent;
    List<ClicksEventDocument> clickEvents;

    public UserStatisticsDocument(Long totalClicks, Long totalCurrencyEarned, Long totalCurrencySpent, List<ClicksEventDocument> clickEvents) {
        this.totalClicks = totalClicks;
        this.totalCurrencyEarned = totalCurrencyEarned;
        this.totalCurrencySpent = totalCurrencySpent;

        this.clickEvents = Objects.requireNonNullElseGet(clickEvents, Collections::emptyList);
    }

    public static UserStatisticsDocument createNew() {
        return new UserStatisticsDocument(0L, 0L, 0L, Collections.emptyList());
    }
}
