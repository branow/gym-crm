package dev.branow.annotations;

import dev.branow.auth.AlwaysAllowAuthorizer;
import dev.branow.auth.Authorizer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authorize {
    Class<? extends Authorizer<?>> value() default AlwaysAllowAuthorizer.class;
}
