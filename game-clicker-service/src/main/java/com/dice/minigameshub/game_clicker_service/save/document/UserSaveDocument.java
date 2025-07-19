package com.dice.minigameshub.game_clicker_service.save.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@Document(collection = "user_game_save")
public class UserSaveDocument {

    @Id
    private long id;
    @Version
    private long version;

    private long currency;
    private int currencyIncomePerMinute;
    private int currencyIncomePerClick;

    @Builder.Default
    private Set<String> purchasedItemsIds = new HashSet<>();
    @Builder.Default
    private Set<String> completedAchievementsIds = new HashSet<>();
    @Builder.Default
    private UserStatisticsDocument userStatistics = UserStatisticsDocument.builder().build();
}
