package com.dice.minigameshub.game_clicker_service.gameconfig;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameConfigRepository extends MongoRepository<GameConfigDocument, String> {

}
