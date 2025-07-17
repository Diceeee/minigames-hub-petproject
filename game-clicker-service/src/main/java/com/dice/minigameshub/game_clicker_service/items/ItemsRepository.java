package com.dice.minigameshub.game_clicker_service.items;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsRepository extends MongoRepository<ItemDocument, String> {

}
