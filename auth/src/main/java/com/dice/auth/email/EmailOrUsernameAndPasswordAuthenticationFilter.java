package com.dice.auth.email;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * Checks if principal in login form is email or username, and creates correct authentication object according to this.
 */

public class EmailOrUsernameAndPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public EmailOrUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                          String loginUri) {
        super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginUri), authenticationManager);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String principal = request.getParameter("principal");
            String password = request.getParameter("password");

            principal = principal != null ? principal.trim() : "";
            password = password != null ? password : "";

            if (EmailValidator.getInstance().isValid(principal)) {
                EmailPasswordAuthenticationToken authRequest = EmailPasswordAuthenticationToken.unauthenticated(principal, password);
                return this.getAuthenticationManager().authenticate(authRequest);
            } else {
                UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(principal, password);
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        }
    }
}
