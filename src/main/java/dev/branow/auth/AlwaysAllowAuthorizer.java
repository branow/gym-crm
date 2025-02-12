package dev.branow.auth;

import org.springframework.stereotype.Component;

@Component
public class AlwaysAllowAuthorizer implements Authorizer<Object> {

    @Override
    public void authorize(Object resource, Credentials credentials) {}
}
