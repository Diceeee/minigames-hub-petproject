package com.dice.minigameshub.game_clicker_service.save.document;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Value
@With
@Builder(toBuilder = true)
@Document(collection = "user_game_save")
public class UserSaveDocument {

    @Id
    Long id;
    @Version
    Long version;

    String lastSessionId;
    Long currency;
    Integer currencyIncomePerMinute;
    Integer currencyIncomePerClick;

    Set<String> purchasedItemsIds;
    Set<String> completedAchievementsIds;

    UserStatisticsDocument userStatistics;

    public UserSaveDocument(Long id, Long version, String lastSessionId, Long currency, Integer currencyIncomePerMinute,
                            Integer currencyIncomePerClick, Set<String> purchasedItemsIds, Set<String> completedAchievementsIds,
                            UserStatisticsDocument userStatistics) {

        this.id = id;
        this.version = version;
        this.lastSessionId = lastSessionId;

        this.currency = Objects.requireNonNullElse(currency, 0L);
        this.currencyIncomePerMinute = Objects.requireNonNullElse(currencyIncomePerMinute, 0);
        this.currencyIncomePerClick = Objects.requireNonNullElse(currencyIncomePerClick, 0);

        this.purchasedItemsIds = Set.copyOf(Objects.requireNonNullElseGet(purchasedItemsIds, Collections::emptySet));
        this.completedAchievementsIds = Set.copyOf(Objects.requireNonNullElseGet(completedAchievementsIds, Collections::emptySet));

        this.userStatistics = Objects.requireNonNullElseGet(userStatistics, UserStatisticsDocument::createNew);
    }
}
