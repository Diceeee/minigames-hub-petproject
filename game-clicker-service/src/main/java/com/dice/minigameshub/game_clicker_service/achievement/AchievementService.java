package com.dice.minigameshub.game_clicker_service.achievement;

import com.dice.minigameshub.game_clicker_service.achievement.target.AchievementTarget;
import com.dice.minigameshub.game_clicker_service.achievement.target.ItemTarget;
import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import com.dice.minigameshub.game_clicker_service.items.ItemsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final ItemsService itemsService;

    public List<AchievementDocument> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public List<AchievementDocument> getAllAchievementsExcept(Collection<String> excludedAchievements) {
        return achievementRepository.findAllByIdNotIn(excludedAchievements);
    }

    public AchievementDocument getById(String achievementId) {
        return achievementRepository.findById(achievementId)
                .orElseThrow(() -> new ServiceException(String.format("Achievement not found by id %s", achievementId), Error.ACHIEVEMENT_NOT_FOUND));
    }

    public AchievementDocument create(AchievementTarget achievementTarget) {
        if (achievementTarget instanceof ItemTarget itemTarget) {
            itemsService.getItemById(itemTarget.getItemId()); // check exists
        }

        AchievementDocument achievementDocument = new AchievementDocument(UUID.randomUUID().toString(), achievementTarget);
        return achievementRepository.save(achievementDocument);
    }

    public AchievementDocument createOrReplace(String id, AchievementTarget achievementTarget) {
        AchievementDocument achievementDocument = new AchievementDocument(id, achievementTarget);
        return achievementRepository.save(achievementDocument);
    }

    // todo: remake or delete, for dev/testing only
    public void deleteAchievement(String achievementId) {
        achievementRepository.deleteById(achievementId);
    }
}
