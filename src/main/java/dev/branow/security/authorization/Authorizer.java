package dev.branow.security.authorization;

import org.springframework.security.core.Authentication;

public interface Authorizer<T> {
    void authorize(T resource, Authentication authentication);
}
