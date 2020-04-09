package com.bloomreach.ps.security;

import com.bloomreach.ps.security.model.SSOUserState;
import org.hippoecm.frontend.model.UserCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jcr.SimpleCredentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class JwtDelegatingSecurityProviderTest {

    private JwtDelegatingSecurityProvider jwtDelegatingSecurityProvider;

    @BeforeEach
    public void init() {
        jwtDelegatingSecurityProvider = spy(JwtDelegatingSecurityProvider.class);
    }

    @Test
    public void whenTheresRequestScopedSSOUserWithJwtUserName_thenCredsArePartOfSSOFlow() {
        final SSOUserState ssoUserState = new SSOUserState(new UserCredentials("foo@bar.com", "dummy"), "sessionId");
        when(jwtDelegatingSecurityProvider.getCurrentSSOUserState()).thenReturn(ssoUserState);
        assertThat(jwtDelegatingSecurityProvider.validateAuthentication(null)).isTrue();
    }

    @Test
    public void whenTheresNoRequestScopedSSOUserButCredsHaveUsernameAsAttribute_thenCredsArePartOfSSOFlow() {
        when(jwtDelegatingSecurityProvider.getCurrentSSOUserState()).thenReturn(null);
        final SimpleCredentials creds = new SimpleCredentials("foo@bar.com", "dummy".toCharArray());
        creds.setAttribute(SSOUserState.JWT_USERNAME, "foo@bar.com");
        assertThat(jwtDelegatingSecurityProvider.validateAuthentication(creds)).isTrue();
    }

    @Test
    public void whenTheresNoRequestScopedSSOUserAndCredsHaveNoUsernameAsAttribute_thenCredsAreNotPartOfSSOFlow() {
        when(jwtDelegatingSecurityProvider.getCurrentSSOUserState()).thenReturn(null);
        final SimpleCredentials creds = new SimpleCredentials("foo@bar.com", "dummy".toCharArray());
        assertThat(jwtDelegatingSecurityProvider.validateAuthentication(creds)).isFalse();
    }

}