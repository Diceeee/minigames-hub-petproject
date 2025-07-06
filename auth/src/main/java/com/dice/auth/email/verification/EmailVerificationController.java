package com.dice.auth.email.verification;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.exception.ApiError;
import com.dice.auth.core.exception.ApiException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/public/email/verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final TokensGenerator tokensGenerator;
    private final CookiesCreator cookiesCreator;

    @GetMapping("{tokenId}")
    public void verifyEmail(@PathVariable String tokenId, HttpServletRequest request, HttpServletResponse response) throws JOSEException {
        EmailVerificationResult emailVerificationResult = emailVerificationService.verifyEmail(tokenId);
        if (emailVerificationResult.isSuccessful()) {
            Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateTokensForUser(emailVerificationResult.getVerifiedUser(),
                    request.getHeader(AuthConstants.Headers.USER_AGENT),
                    AuthUtils.getClientIpAddress(request));

            Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
            Cookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        } else {
            switch (emailVerificationResult.getError()) {
                case TOKEN_NOT_FOUND -> throw new ApiException("Token not found by token id " + tokenId, ApiError.EMAIL_VERIFICATION_TOKEN_NOT_FOUND);
                case EMAIL_ALREADY_VERIFIED -> throw new ApiException("User already has verified email", ApiError.EMAIL_VERIFICATION_ALREADY_VERIFIED);
                default -> throw new ApiException("Unknown exception during email verification happened: " + emailVerificationResult.getError(), ApiError.UNKNOWN);
            }
        }
    }
}
