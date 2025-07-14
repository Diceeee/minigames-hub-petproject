package com.dice.minigameshub.game_clicker_service.save.document;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Document(collection = "user_game_save")
public class UserSaveDocument {

    @Id
    Long id;

    Long currency;
    List<String> purchasedItemsIds;
    UserStatisticsDocument userStatisticsDocument;
}
