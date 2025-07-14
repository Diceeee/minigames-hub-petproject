package com.dice.minigameshub.game_clicker_service.items;

import com.dice.minigameshub.game_clicker_service.items.document.ItemDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<ItemDocument, String> {

}
