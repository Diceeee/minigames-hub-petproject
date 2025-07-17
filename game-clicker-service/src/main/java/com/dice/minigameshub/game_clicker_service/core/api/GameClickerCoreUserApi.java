package com.dice.minigameshub.game_clicker_service.core.api;

import com.dice.minigameshub.game_clicker_service.common.Headers;
import com.dice.minigameshub.game_clicker_service.common.UserDetails;
import com.dice.minigameshub.game_clicker_service.core.CoreGameMapper;
import com.dice.minigameshub.game_clicker_service.core.CoreGameService;
import com.dice.minigameshub.game_clicker_service.core.api.dto.clicks.ClicksRequest;
import com.dice.minigameshub.game_clicker_service.core.api.dto.clicks.ClicksResponse;
import com.dice.minigameshub.game_clicker_service.core.api.dto.start.StartGameResponse;
import com.dice.minigameshub.game_clicker_service.core.dto.clicks.ClicksProcessInput;
import com.dice.minigameshub.game_clicker_service.core.dto.start.StartGameInput;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/public")
public class GameClickerCoreUserApi {

    private final CoreGameService coreGameService;
    private final CoreGameMapper coreGameMapper;

    @PostMapping("/clicks")
    public ClicksResponse clicks(@RequestBody @Valid ClicksRequest clicksRequest,
                                 @RequestHeader(Headers.USER_ID) Long userId,
                                 @RequestHeader(Headers.SESSION_ID) String sessionId) {

        UserDetails userDetails = UserDetails.of(userId, sessionId);
        return coreGameMapper.mapToResponse(coreGameService.processClicks(
                ClicksProcessInput.builder()
                        .clicks(clicksRequest.getClicks())
                        .userDetails(userDetails)
                        .build()));
    }

    @PostMapping("/start")
    public StartGameResponse startGame(@RequestHeader(Headers.USER_ID) Long userId,
                                       @RequestHeader(Headers.SESSION_ID) String sessionId) {

        UserDetails userDetails = UserDetails.of(userId, sessionId);
        return coreGameMapper.mapToResponse(coreGameService.startGame(
                StartGameInput.builder()
                        .userDetails(userDetails)
                        .build()));
    }
}
