package com.dice.minigameshub.game_clicker_service.items;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "items")
public class ItemDocument {

    @Id
    private String id;
    private String name;
    private String description;

    private Integer price;
    private Integer currencyIncomeIncreaseInMinute;
    private Integer currencyIncomeIncreasePerClick;
}
