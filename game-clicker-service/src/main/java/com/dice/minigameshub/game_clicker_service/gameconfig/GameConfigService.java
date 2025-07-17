package com.dice.minigameshub.game_clicker_service.gameconfig;

import com.dice.minigameshub.game_clicker_service.common.exception.Error;
import com.dice.minigameshub.game_clicker_service.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GameConfigService {

    private final GameConfigRepository gameConfigRepository;
    private final GameConfigMapper gameConfigMapper;

    public GameConfig getGameConfig() {
        GameConfigDocument document = gameConfigRepository.findById(GameConfigDocument.GAME_CONFIG_DOCUMENT_ID)
                .orElseThrow(() -> new ServiceException("Missing game config document", Error.GAME_NOT_CONFIGURED));

        return gameConfigMapper.mapDocument(document);
    }
}
