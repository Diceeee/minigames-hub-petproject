package com.dice.auth.token.refresh;

import com.dice.auth.AuthConstants;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.core.util.AuthUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Silently refreshes access/refresh tokens by redirecting to refresh only for GET requests
 * and if access token is going to be expired soon, determined by window minutes config.
 * <p>
 * This kind of implementation allow to achieve next goals:
 * 1. Refresh is always silent, because GET requests are easily redirectable comparing to other HTTP methods requests, that should guarantee good user-experience.
 * 2. Refresh window allows to regenerate refresh/access tokens only if it is going to expire soon, avoiding too much frequent refreshes.
 * 3. Refresh token is not forced to be sent to each request, it is still limited to be sent only to refresh URI, that is good to reduce risks of refresh token stealing.
 *
 * <p>
 * How it works:
 * 1) User does get requests to any endpoint
 * 2) Is user's access token will be expired soon?
 *  a) Yes -> Redirect to refresh endpoint and save user's original destination for future redirection.
 *  b) No -> Continue request.
 */
// todo: should be extracted to gateway in future
@Slf4j
@AllArgsConstructor
public class RefreshAccessTokenRedirectionFilter extends OncePerRequestFilter {

    private final RefreshValidator refreshValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AuthConstants.Cookies.ACCESS_TOKEN)) {
                    accessToken = cookie.getValue();
                }
            }
        }

        if (accessToken != null && HttpMethod.GET.matches(request.getMethod()) && !request.getRequestURI().contains(AuthConstants.Uris.REFRESH)) {
            try {
                if (refreshValidator.validateRefreshAllowed(accessToken)) {
                    response.sendRedirect(String.format("%s?redirectTo=%s", AuthConstants.Uris.REFRESH, AuthUtils.getOriginalUrl(request)));
                    return;
                }
            } catch (Exception ignored) {

            }
        }

        filterChain.doFilter(request, response);
    }
}
