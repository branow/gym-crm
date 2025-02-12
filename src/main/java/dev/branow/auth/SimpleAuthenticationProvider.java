package dev.branow.auth;

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
    public void authenticate(Credentials credentials) {
        authenticationContext.setCredentials(credentials);

        var user = userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(this::handleAuthenticationFailure);

        if (!user.getPassword().equals(credentials.getPassword())) {
            throw handleAuthenticationFailure();
        }

        authenticationContext.setAuthenticated(true);
    }

    private BadCredentialsException handleAuthenticationFailure() {
        authenticationContext.setAuthenticated(false);
        return new BadCredentialsException();
    }

}
