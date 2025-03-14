package dev.branow.aspects;

import dev.branow.annotations.Authorize;
import dev.branow.security.authorization.Authorizer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    @Lazy
    private final ApplicationContext applicationContext;

    @Around("@annotation(authorize)")
    public Object authorize(ProceedingJoinPoint joinPoint, Authorize authorize) throws Throwable {
        Object resource = extractResource(joinPoint.getArgs());
        Authentication authentication = Optional.of(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> new AccessDeniedException("User is not authenticated."));
        Authorizer<Object> authorizer = getAuthorizer(authorize.value());
        authorizer.authorize(resource, authentication);
        return joinPoint.proceed();
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
