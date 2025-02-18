package dev.branow.auth.authorizers;

import dev.branow.annotations.Log;
import dev.branow.auth.Credentials;
import dev.branow.exceptions.AccessDeniedException;
import dev.branow.model.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
public class SimpleUserAuthorizer {

    @Transactional
    @Log("authorization")
    public void authorize(Credentials credentials, Supplier<User> supplier) {
        User user = Objects.requireNonNull(supplier.get(), "User not found");

        if (!user.getUsername().equals(credentials.getUsername())) {
            throw new AccessDeniedException("You are not allowed to access this user");
        }
    }

}
