package com.dice.minigameshub.game_clicker_service.achievement.api;

import com.dice.minigameshub.game_clicker_service.achievement.AchievementMapper;
import com.dice.minigameshub.game_clicker_service.achievement.AchievementService;
import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementRequest;
import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/achievement")
public class AchievementAdminApi {

    private final AchievementService achievementService;
    private final AchievementMapper achievementMapper;

    @PutMapping("{achievementId}")
    public AchievementResponse createOrReplace(@PathVariable String achievementId, @RequestBody AchievementRequest request) {
        return achievementMapper.mapToResponse(achievementService.createOrReplace(achievementId, request.getTarget()));
    }

    @PostMapping
    public AchievementResponse create(@RequestBody AchievementRequest request) {
        return achievementMapper.mapToResponse(achievementService.create(request.getTarget()));
    }

    @DeleteMapping("{achievementId}")
    public void delete(@PathVariable String achievementId) {
        achievementService.deleteAchievement(achievementId);
    }
}
