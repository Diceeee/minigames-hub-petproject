package com.dice.auth;

import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@AllArgsConstructor
public class AuthServiceController implements ErrorController {

    private final UserService userService;
    private final JavaMailSender mailSender;

    @GetMapping("/")
    public String home(@RequestHeader(name = "X-User-Id", required = false) String userIdHeader, Authentication authentication, Model model) {
        System.out.println("Home, userId: " + userIdHeader);
        log.info("Home, userId: " + userIdHeader);
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Long userId = Long.valueOf(jwt.getSubject());
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
        }

        return "home";
    }

    @GetMapping("/login")
    public String loginPage(@Nullable Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:" + AuthConstants.Uris.HOME;
        }

        return AuthConstants.Uris.LOGIN;
    }

    @RequestMapping("/error")
    public String error(HttpServletRequest request) {
        log.info("Error page routed for request: method = {}, uri = {}", request.getMethod(), AuthUtils.getOriginalUrl(request));
        return "redirect:" + AuthConstants.Uris.HOME;
    }
}
