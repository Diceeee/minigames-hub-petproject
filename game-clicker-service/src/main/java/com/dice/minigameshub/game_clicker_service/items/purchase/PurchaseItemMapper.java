package com.dice.minigameshub.game_clicker_service.items.purchase;

import com.dice.minigameshub.game_clicker_service.items.api.dto.PurchaseItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public interface PurchaseItemMapper {

    PurchaseItemResponse mapPurchaseItemResponse(PurchaseItemResult purchaseItemResult);
}
