package com.bloomreach.ps.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    public void init() {
        jwtAuthenticationFilter = spy(JwtAuthenticationFilter.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void whenFilterInvoked_thenCorrectHeaderChecked() {
        try {
            jwtAuthenticationFilter.attemptAuthentication(request, response);
        } catch (InvalidJwtTokenException ignored) {
        }
        verify(request).getHeader("Authorization");
    }

    @Test()
    public void whenAuthorizationHeaderEmpty_thenInvalidJwtExceptionThrown() {
        when(request.getHeader(anyString())).thenReturn("");
        assertThatExceptionOfType(InvalidJwtTokenException.class).isThrownBy(() -> jwtAuthenticationFilter.attemptAuthentication(request, response));
    }

    @Test
    public void whenAuthorizationHeaderNull_thenInvalidJwtExceptionThrown() {
        when(request.getHeader(anyString())).thenReturn(null);
        assertThatExceptionOfType(InvalidJwtTokenException.class).isThrownBy(() -> jwtAuthenticationFilter.attemptAuthentication(request, response));
    }

    @Test
    public void whenAuthorizationHeaderPassedCorrectly_thenJwtAuthenticationTokenCreated() {
        final String token = "CorrectTokenInBase64";
        final String headerValue = "Bearer " + token;
        when(request.getHeader(anyString())).thenReturn(headerValue);
        try {
            jwtAuthenticationFilter.attemptAuthentication(request, response);
        } catch (NullPointerException ignored) {
            //ignoring the NPE on getAuthManager()
        }
        verify(jwtAuthenticationFilter).getJwtAuthenticationTokenFromTokenString(token);
    }

}
