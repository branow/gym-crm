package dev.branow.auth;

public interface AuthenticationProvider {

    void authenticate(Credentials credentials);

}
