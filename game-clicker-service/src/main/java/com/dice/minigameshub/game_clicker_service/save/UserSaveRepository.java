package com.dice.minigameshub.game_clicker_service.save;

import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSaveRepository extends MongoRepository<UserSaveDocument, Long> {

}
