package dev.branow.security.authorization;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AlwaysAllowAuthorizer implements Authorizer<Object> {

    @Override
    public void authorize(Object resource, Authentication authentication) {}

}
