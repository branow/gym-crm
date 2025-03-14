package dev.branow.security;

import dev.branow.exceptions.TokenRevokedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtAuthenticationProvider provider;
    private final JwtRevocationService revocationService;

    public CustomJwtAuthenticationProvider(JwtDecoder jwtDecoder, JwtRevocationService jwtRevocationService) {
        this.provider = new JwtAuthenticationProvider(jwtDecoder);
        this.revocationService = jwtRevocationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var jwt = (String) authentication.getPrincipal();
        if (revocationService.isRevoked(jwt))
            throw new TokenRevokedException();
        return provider.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return provider.supports(authentication);
    }

}
