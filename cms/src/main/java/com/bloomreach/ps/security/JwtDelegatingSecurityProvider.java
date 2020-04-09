package com.bloomreach.ps.security;

import com.bloomreach.ps.security.model.SSOUserState;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.value.StringValue;
import org.hippoecm.repository.security.DelegatingSecurityProvider;
import org.hippoecm.repository.security.RepositorySecurityProvider;
import org.hippoecm.repository.security.user.DelegatingHippoUserManager;
import org.hippoecm.repository.security.user.HippoUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.Arrays;

/**
 * Custom <code>org.hippoecm.repository.security.SecurityProvider</code> implementation.
 * <p>
 * Hippo Repository allows to set a custom security provider for various reasons (e.g, SSO) for specific users.
 * If a user is associated with a custom security provider, then brXM Repository invokes
 * the custom security provider to do authentication and authorization.
 * </P>
 */
public class JwtDelegatingSecurityProvider extends DelegatingSecurityProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtDelegatingSecurityProvider.class);

    private HippoUserManager userManager;
    private static final String SECURITY_PROVIDER = "jwt";
    private static final String HIPPOSYS_SECURITY_PROVIDER = "hipposys:securityprovider";
    private static final String HIPPOSYS_ACTIVE = "hipposys:active";
    private static final String HIPPOSYS_MEMBERS = "hipposys:members";

    /**
     * Constructs by creating the default <code>RepositorySecurityProvider</code> to delegate all the other calls
     * except of authentication calls.
     *
     * @throws RepositoryException
     */
    public JwtDelegatingSecurityProvider() throws RepositoryException {
        super(new RepositorySecurityProvider());
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by JWT
     */
    @Override
    public UserManager getUserManager() throws RepositoryException {
        if (userManager == null) {
            userManager = new JwtDelegatingHippoUserManager((HippoUserManager) super.getUserManager());
        }
        return userManager;
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by JWT
     */
    @Override
    public UserManager getUserManager(final Session session) throws RepositoryException {
        if (userManager == null) {
            userManager = new JwtDelegatingHippoUserManager((HippoUserManager) super.getUserManager(session));
        }
        return userManager;
    }

    /**
     * This method makes sure that the credentials are coming as part of the jwt sso flow.
     */
    protected boolean validateAuthentication(final SimpleCredentials creds) {
        final SSOUserState userState = getCurrentSSOUserState();
        if (userState != null) {
            return StringUtils.isNotEmpty(userState.getCredentials().getUsername());
        }
        final String jwtUsername = (String) creds.getAttribute(SSOUserState.JWT_USERNAME);
        if (StringUtils.isNotBlank(jwtUsername)) {
            log.debug("Authentication allowed to: {}", jwtUsername);
            return true;
        }
        log.debug("Authentication disallowed to: {}", jwtUsername);
        return false;
    }

    protected SSOUserState getCurrentSSOUserState() {
        return JwtAuthenticationFilter.getCurrentSSOUserState();
    }

    protected void syncUserInfoWithRepository(final Node user, final Node group) throws RepositoryException {
        user.setProperty(HIPPOSYS_SECURITY_PROVIDER, SECURITY_PROVIDER);
        user.setProperty(HIPPOSYS_ACTIVE, true);
        final Value[] values = group.getProperties(HIPPOSYS_MEMBERS).nextProperty().getValues();
        final Value[] newValues = Arrays.copyOf(values, values.length + 1);
        newValues[values.length] = new StringValue(user.getName());
        group.setProperty(HIPPOSYS_MEMBERS, newValues);
    }

    private class JwtDelegatingHippoUserManager extends DelegatingHippoUserManager {

        public JwtDelegatingHippoUserManager(final HippoUserManager delegatee) {
            super(delegatee);
        }

        @Override
        public boolean authenticate(final SimpleCredentials creds) throws RepositoryException {
            //  The authentication request might have come from a brxm login with ANY password.
            //  We need to know whether the request is part of the SSO flow or not
            if (validateAuthentication(creds)) {
                final String userId = creds.getUserID();
                //TODO don't add everyone to admin group! Depending on usecase/project, read it from creds.
                //TODO if v14+, read userroles from jwt and put them on user nodes. Much cleaner!
                if (!hasUser(userId)) {
                    syncUserInfoWithRepository(createUser(userId), getGroupManager().getGroup("admin"));
                }
                return true;
            } else return false;
        }
    }
}