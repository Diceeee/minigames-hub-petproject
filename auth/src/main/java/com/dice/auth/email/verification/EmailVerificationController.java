package com.dice.auth.email.verification;

import com.dice.auth.AuthConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @GetMapping("/email/verification/{tokenId}")
    public String verifyEmail(@PathVariable String tokenId) {
        emailVerificationService.verifyEmail(tokenId);
        return "redirect:" + AuthConstants.Uris.LOGIN;
    }
}
