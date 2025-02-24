package dev.branow.auth;

import org.springframework.stereotype.Component;

@Component
public class SimpleAuthenticationContext implements AuthenticationContext {

    private Credentials credentials;
    private boolean authenticated;

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void expire() {
        authenticated = false;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

}
