package com.dice.auth.email.verification;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.token.TokensGenerator;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final TokensGenerator tokensGenerator;
    private final CookiesCreator cookiesCreator;

    @GetMapping("/email/verification/{tokenId}")
    public String verifyEmail(@PathVariable String tokenId, HttpServletRequest request, HttpServletResponse response) throws JOSEException {
        EmailVerificationResult emailVerificationResult = emailVerificationService.verifyEmail(tokenId);
        if (emailVerificationResult.successful()) {
            Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateTokensForUser(emailVerificationResult.verifiedUser(),
                    request.getHeader(AuthConstants.Headers.USER_AGENT),
                    AuthUtils.getClientIpAddress(request));

            Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
            Cookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return "redirect:" + AuthConstants.Uris.HOME;
        }

        return "redirect:" + AuthConstants.Uris.LOGIN;
    }
}
