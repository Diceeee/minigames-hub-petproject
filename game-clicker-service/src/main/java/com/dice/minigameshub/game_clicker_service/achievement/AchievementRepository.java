package com.dice.minigameshub.game_clicker_service.achievement;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AchievementRepository extends MongoRepository<AchievementDocument, String> {

    List<AchievementDocument> findAllByIdNotIn(Collection<String> ids);
}
