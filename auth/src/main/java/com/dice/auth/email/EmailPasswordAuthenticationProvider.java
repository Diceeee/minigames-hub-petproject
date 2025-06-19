package com.dice.auth.email;

import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EmailPasswordAuthenticationToken emailPasswordAuth = (EmailPasswordAuthenticationToken) authentication;

        if (emailPasswordAuth.getCredentials() == null) {
           log.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException("Bad credentials");
        }

        User user = userService.getUserByEmail(emailPasswordAuth.getPrincipal().toString());
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, user.getPassword())) {
            log.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }

        EmailPasswordAuthenticationToken result = EmailPasswordAuthenticationToken.authenticated(user,
                emailPasswordAuth.getCredentials().toString(), user.getAuthorities());

        result.setDetails(authentication.getDetails());
        log.debug("Authenticated user: {}", user);
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
