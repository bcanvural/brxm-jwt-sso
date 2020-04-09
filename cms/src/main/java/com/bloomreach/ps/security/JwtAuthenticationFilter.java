package com.bloomreach.ps.security;


import com.bloomreach.ps.security.model.AuthenticatedUser;
import com.bloomreach.ps.security.model.JwtAuthenticationToken;
import com.bloomreach.ps.security.model.SSOUserState;
import org.hippoecm.frontend.model.UserCredentials;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.jcr.SimpleCredentials;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String SSO_USER_STATE = SSOUserState.class.getName();

    private static ThreadLocal<SSOUserState> tlCurrentSSOUserState = new ThreadLocal<>();

    public static SSOUserState getCurrentSSOUserState() {
        return tlCurrentSSOUserState.get();
    }

    public JwtAuthenticationFilter() {
        super("/**");
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidJwtTokenException("No JWT token found in request headers");
        }
        final String authToken = header.substring(7);

        final JwtAuthenticationToken jwtAuthenticationToken = getJwtAuthenticationTokenFromTokenString(authToken);
        return getAuthenticationManager().authenticate(jwtAuthenticationToken);
    }

    protected JwtAuthenticationToken getJwtAuthenticationTokenFromTokenString(final String authToken) {
        return new JwtAuthenticationToken(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        final AuthenticatedUser authenticatedUser = extractUserFromAuthResult(authResult);
        if (authenticatedUser != null) {
            prepareSsoUserForCms(request, authenticatedUser.getUsername());
        }
        try {
            chain.doFilter(request, response);
        } finally {
            tlCurrentSSOUserState.remove();
        }
    }

    private AuthenticatedUser extractUserFromAuthResult(final Authentication authResult) {
        final Object principal = authResult.getPrincipal();
        if (principal instanceof AuthenticatedUser) {
            return (AuthenticatedUser) principal;
        } else return null;
    }


    private void prepareSsoUserForCms(final HttpServletRequest request, final String username) {
        final HttpSession session = request.getSession();
        SSOUserState userState = (SSOUserState) session.getAttribute(SSO_USER_STATE);
        final boolean userHasSSOUserState = userState == null || !userState.getSessionId().equals(session.getId());
        if (userHasSSOUserState) {
            // Using a dummy string as password which must not be an empty string, as this is mandatory
            final SimpleCredentials creds = new SimpleCredentials(username, "DUMMY".toCharArray());
            creds.setAttribute(SSOUserState.JWT_USERNAME, username); //to check later in SecurityProvider's validateAuthentication
            userState = new SSOUserState(new UserCredentials(creds), session.getId());
            session.setAttribute(SSO_USER_STATE, userState);
        }
        // If the user has a valid SSO user state, then
        // set a JCR Credentials as request attribute (named by FQCN of UserCredentials class).
        // Then the CMS application will use the JCR credentials passed through this request attribute.
        if (userHasSSOUserState) {
            request.setAttribute(UserCredentials.class.getName(), userState.getCredentials());
        }
        tlCurrentSSOUserState.set(userState);
    }
}
