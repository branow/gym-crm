package dev.branow.auth;

public interface AuthenticationContext {

    Credentials getCredentials();
    boolean isAuthenticated();
    void expire();

}
