package com.dice.minigameshub.game_clicker_service.achievement.api;

import com.dice.minigameshub.game_clicker_service.achievement.AchievementMapper;
import com.dice.minigameshub.game_clicker_service.achievement.AchievementService;
import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/public/achievement")
public class AchievementUserApi {

    private final AchievementService achievementService;
    private final AchievementMapper achievementMapper;

    @GetMapping
    public List<AchievementResponse> getAllAchievements() {
        return achievementService.getAllAchievements().stream()
                .map(achievementMapper::mapToResponse)
                .toList();
    }

    @GetMapping("{achievementId}")
    public AchievementResponse get(@PathVariable String achievementId) {
        return achievementMapper.mapToResponse(achievementService.getById(achievementId));
    }
}
