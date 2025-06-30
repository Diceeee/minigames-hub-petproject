package com.dice.auth.registration;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Validated
@Controller
@AllArgsConstructor
@RequestMapping(path = "register")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final CookiesCreator cookiesCreator;
    private final UserService userService;

    @GetMapping
    public String registration(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("context", "regular");
            return "register";
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = Long.valueOf(jwt.getSubject());

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("context", "oauth2");

        return "register";
    }

    @PostMapping("cancel")
    public String cancel(Authentication authentication, HttpServletResponse response) {
        if (authentication != null) {
            Cookie accessTokenCookie = cookiesCreator.getDeletedAccessTokenCookie();
            Cookie refreshTokenCookie = cookiesCreator.getDeletedRefreshTokenCookie();

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        }

        return "redirect:" + AuthConstants.Uris.LOGIN;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String processRegistration(@ModelAttribute @Valid RegistrationDto registration, HttpServletRequest httpServletRequest,
                                      HttpServletResponse response, Authentication authentication) {
        try {
            RegistrationResult registrationResult = registrationService.register(registration);

            if (registrationResult.isSuccessful()) {
                if (registrationResult.getUpdatedAccessToken() != null) {
                    Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(registrationResult.getUpdatedAccessToken());
                    response.addCookie(accessTokenCookie);
                }

                if (registrationResult.getRegisteredUser().isEmailVerified()) {
                    return "redirect:" + AuthConstants.Uris.HOME;
                } else {
                    return "verify-email";
                }
            } else {
                return "redirect:" + AuthUtils.getOriginalUrl(httpServletRequest) + "&error=" + registrationResult.getErrorId();
            }
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
}
