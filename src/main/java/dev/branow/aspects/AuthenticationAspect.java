package dev.branow.aspects;

import dev.branow.annotations.Authenticate;
import dev.branow.auth.AuthenticationContext;
import dev.branow.auth.AuthenticationProvider;
import dev.branow.auth.CredentialsProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationAspect {

    private final CredentialsProvider credentialsProvider;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationContext authenticationContext;

    @Around("@annotation(authenticate)")
    public Object authenticate(ProceedingJoinPoint joinPoint, Authenticate authenticate) throws Throwable {
        var thisMethodIsAuthenticator = false;

        if (!authenticationContext.isAuthenticated()) {
            thisMethodIsAuthenticator = true;
            authenticateUser();
        }

        try {
            return joinPoint.proceed();
        } finally {
            if (thisMethodIsAuthenticator) {
                authenticationContext.expire();
            }
        }
    }

    private void authenticateUser() {
        var credentials = credentialsProvider.getCredentials();
        authenticationProvider.authenticate(credentials);
    }

}
