package com.dice.minigameshub.game_clicker_service.items.document;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document(collection = "items")
public class ItemDocument {

    @Id
    String id;
    String name;
    String description;

    Integer price;
    Integer passiveIncomeIncreaseInMinute;
    Integer activeIncomeIncreasePerClick;
}
