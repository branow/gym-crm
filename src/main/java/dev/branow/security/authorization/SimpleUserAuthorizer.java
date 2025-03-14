package dev.branow.security.authorization;

import dev.branow.annotations.Log;
import dev.branow.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
public class SimpleUserAuthorizer implements UserAuthorizer<Supplier<User>> {

    @Override
    @Transactional
    @Log("authorization")
    public void authorize(Supplier<User> resource, Authentication authentication) {
        User user = Objects.requireNonNull(resource.get());
        if (!user.getUsername().equals(authentication.getName())) {
            throw new AccessDeniedException("You are not allowed to perform the operation on user " + user.getUsername());
        }
    }

}
