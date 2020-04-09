package com.bloomreach.ps.security.model;

import org.hippoecm.frontend.model.UserCredentials;

import java.io.Serializable;

public class SSOUserState implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String JWT_USERNAME = SSOUserState.class.getName() + ".jwt.username";

    private final UserCredentials credentials;
    private final String sessionId;

    public SSOUserState(final UserCredentials credentials, final String sessionId) {
        this.credentials = credentials;
        this.sessionId = sessionId;
    }

    public UserCredentials getCredentials() {
        return credentials;
    }

    public String getSessionId() {
        return sessionId;
    }
}