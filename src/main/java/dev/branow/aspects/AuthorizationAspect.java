package dev.branow.aspects;

import dev.branow.annotations.Authorize;
import dev.branow.auth.AuthenticationContext;
import dev.branow.auth.Authorizer;
import dev.branow.exceptions.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final AuthenticationContext authenticationContext;
    @Lazy
    private final ApplicationContext applicationContext;

    @Around("@annotation(authorize)")
    public Object authorize(ProceedingJoinPoint joinPoint, Authorize authorize) throws Throwable {
        ensureAuthenticated();

        Object resource = extractResource(joinPoint.getArgs());
        var credentials = authenticationContext.getCredentials();

        Authorizer<Object> authorizer = getAuthorizer(authorize.value());
        authorizer.authorize(resource, credentials);

        return joinPoint.proceed();
    }

    private void ensureAuthenticated() {
        if (!authenticationContext.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated. Access Denied.");
        }
    }

    private Object extractResource(Object[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Method requires at least one argument representing the resource to authorize.");
        }
        return args[0];
    }

    private Authorizer<Object> getAuthorizer(Class<? extends Authorizer<?>> authorizerClass) {
        return (Authorizer<Object>) applicationContext.getBean(authorizerClass);
    }


}
