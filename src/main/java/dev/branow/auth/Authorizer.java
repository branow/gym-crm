package dev.branow.auth;

public interface Authorizer<T> {

    void authorize(T resource, Credentials credentials);

}
