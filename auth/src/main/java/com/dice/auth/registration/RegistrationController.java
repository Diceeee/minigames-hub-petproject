package com.dice.auth.registration;

import com.dice.auth.AuthConstants;
import com.dice.auth.core.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@Controller
@AllArgsConstructor
@RequestMapping(path = "register")
public class RegistrationController {

    private final RegistrationService registrationService;

    /**
     * Client does not always save cookies immediately after they are set, it must end redirection chain first cause tokens cookies are Strict secured.
     * So for this scenario, this endpoint is needed to manually send pre-saved registration data, as example after login via external provider as Google.
     */
    @GetMapping(path = "/continue")
    public String registrationContinue(@RequestParam(value = "email", required = false) String email,
                                       @RequestParam(value = "nickname", required = false) String nickname,
                                       @RequestParam(value = "username", required = false) String username,
                                       Model model) {

        if (email != null) {
            model.addAttribute("email", email);
        }
        if (nickname != null) {
            model.addAttribute("nickname", nickname);
        }
        if (username != null) {
            model.addAttribute("username", username);
        }

        return "register";
    }

    @GetMapping
    public String registrationPage() {
        return "register";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String processRegistration(@ModelAttribute @Valid RegistrationDto registration, HttpServletRequest httpServletRequest) {
        RegistrationResult registrationResult = registrationService.register(registration);
        if (registrationResult.isSuccessful()) {
            return "redirect:" + AuthConstants.Uris.HOME;
        } else {
            return "redirect:" + AuthUtils.getOriginalUrl(httpServletRequest) + "&error=" + registrationResult.getErrorId();
        }
    }
}
