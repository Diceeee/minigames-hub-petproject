package com.dice.auth.user;

import com.dice.auth.user.domain.User;
import com.dice.auth.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true, noRollbackFor = UserNotFoundException.class)
    public User getUserById(Long userId) throws UserNotFoundException {
        return userMapper.mapUserEntity(userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.forId(userId)));
    }

    @Transactional
    public void removeUserById(Long userId) {
        log.info("Deleting user with id {}", userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true, noRollbackFor = UserNotFoundException.class)
    public User getUserByEmail(String email) throws UserNotFoundException {
        return userMapper.mapUserEntity(userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.forEmail(email)));
    }

    @Transactional(readOnly = true, noRollbackFor = UserNotFoundException.class)
    public User getUserByUsername(String username) throws UserNotFoundException {
        return userMapper.mapUserEntity(userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.forUsername(username)));
    }

    @Transactional(readOnly = true)
    public boolean userExistWithUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User save(User user) {
        return userMapper.mapUserEntity(userRepository.save(userMapper.mapUserToEntity(user)));
    }
}
