package com.dice.auth.token.refresh.exception;

public class SessionAssociatedWithRefreshTokenNotFound extends GenericRefreshTokenException {

    private final static String TOKEN_ID_MSG_TEMPLATE = "No session found for token id '%s'";

    public SessionAssociatedWithRefreshTokenNotFound(String message) {
        super(message, RefreshTokenExceptionErrorCode.TOKEN_NOT_FOUND);
    }

    public static SessionAssociatedWithRefreshTokenNotFound forTokenId(String tokenId) {
        return new SessionAssociatedWithRefreshTokenNotFound(String.format(TOKEN_ID_MSG_TEMPLATE, tokenId));
    }
}
