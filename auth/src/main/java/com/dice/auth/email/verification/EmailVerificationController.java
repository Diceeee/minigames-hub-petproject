package com.dice.auth.email.verification;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.common.exception.ServiceError;
import com.dice.auth.common.exception.ServiceException;
import com.dice.auth.core.properties.AuthConfigurationProperties;
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

import java.io.IOException;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/api/public/email/verification")
public class EmailVerificationController {

    private final AuthConfigurationProperties authProperties;
    private final EmailVerificationService emailVerificationService;
    private final TokensGenerator tokensGenerator;
    private final CookiesCreator cookiesCreator;

    @GetMapping("{tokenId}")
    public void verifyEmail(@PathVariable String tokenId, HttpServletRequest request, HttpServletResponse response) throws JOSEException, IOException {
        EmailVerificationResult emailVerificationResult = emailVerificationService.verifyEmail(tokenId);
        if (emailVerificationResult.isSuccessful()) {
            Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateTokensForUser(emailVerificationResult.getVerifiedUser(),
                    request.getHeader(AuthConstants.Headers.USER_AGENT),
                    AuthUtils.getClientIpAddress(request));

            Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
            Cookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            response.sendRedirect(authProperties.getFrontendUrl());
        } else {
            switch (emailVerificationResult.getError()) {
                case TOKEN_NOT_FOUND -> throw new ServiceException("Token not found by token id " + tokenId, ServiceError.EMAIL_VERIFICATION_TOKEN_NOT_FOUND);
                case EMAIL_ALREADY_VERIFIED -> throw new ServiceException("User already has verified email", ServiceError.EMAIL_VERIFICATION_ALREADY_VERIFIED);
                default -> throw new ServiceException("Unknown exception during email verification happened: " + emailVerificationResult.getError(), ServiceError.UNKNOWN);
            }
        }
    }
}
