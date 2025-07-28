package com.dice.auth;

import com.dice.auth.core.access.Roles;
import com.dice.auth.user.UserService;
import com.dice.auth.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Only needed for dev/testing because docker-compose scripts are run once and can't be run after Liquibase migrations rollup.
 * This class is absolutely not for production.
 */
@Slf4j
@Component
@Profile("dev")
@AllArgsConstructor
public class DevOnlyTestUserCreator implements ApplicationListener<ApplicationStartedEvent> {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final Clock clock;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (!userService.userExistWithUsername("test")) {
            log.info("Initializing test user");
            userService.save(User.builder()
                    .username("test")
                    .password(passwordEncoder.encode("test"))
                    .email("test@test.com")
                    .emailVerified(true)
                    .registered(true)
                    .authority(Roles.ADMIN.getRoleWithPrefix())
                    .authority(Roles.USER.getRoleWithPrefix())
                    .createdAt(clock.instant())
                    .build());
        }
    }
}
