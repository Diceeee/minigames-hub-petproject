package com.dice.minigameshub.game_clicker_service.save;

import com.dice.minigameshub.game_clicker_service.common.exception.ApiError;
import com.dice.minigameshub.game_clicker_service.common.exception.ApiException;
import com.dice.minigameshub.game_clicker_service.save.document.UserSaveDocument;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserSaveService {

    private final UserSaveRepository userSaveRepository;

    public UserSaveDocument getUserSave(Long userId) {
        return userSaveRepository.findById(userId)
                .orElseThrow(() -> new ApiException(String.format("User's %d save not found", userId), ApiError.USER_SAVE_NOT_FOUND));
    }

    public UserSaveDocument saveDocument(UserSaveDocument userSaveDocument) {
        return userSaveRepository.save(userSaveDocument);
    }

    public boolean isUserGameSaveExists(Long userId) {
        return userSaveRepository.existsById(userId);
    }
}
