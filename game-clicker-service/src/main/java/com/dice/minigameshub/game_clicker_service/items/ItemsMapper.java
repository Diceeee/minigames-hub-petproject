package com.dice.minigameshub.game_clicker_service.items;

import com.dice.minigameshub.game_clicker_service.items.api.dto.ItemResponse;
import com.dice.minigameshub.game_clicker_service.items.api.dto.PurchaseItemResponse;
import com.dice.minigameshub.game_clicker_service.items.dto.purchase.PurchaseItemResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public interface ItemsMapper {

    PurchaseItemResponse mapToResponse(PurchaseItemResult purchaseItemResult);
    ItemResponse mapToResponse(ItemDocument itemDocument);
}
