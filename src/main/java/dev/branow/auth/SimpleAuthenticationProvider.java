package dev.branow.auth;

import dev.branow.annotations.Log;
import dev.branow.exceptions.BadCredentialsException;
import dev.branow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleAuthenticationProvider implements AuthenticationProvider {

    private final SimpleAuthenticationContext authenticationContext;
    private final UserRepository userRepository;

    @Override
    @Log("authentication")
    public void authenticate(Credentials credentials) {
        authenticationContext.setCredentials(credentials);

        userRepository.findByUsername(credentials.getUsername())
                .filter(u -> u.getPassword().equals(credentials.getPassword()))
                .orElseThrow(() -> {
                    authenticationContext.setAuthenticated(false);
                    return new BadCredentialsException();
                });

        authenticationContext.setAuthenticated(true);
    }

}
