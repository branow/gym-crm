package dev.branow.security;

import dev.branow.exceptions.TokenRevokedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(CustomJwtAuthenticationProvider.class)
public class CustomJwtAuthenticationProviderTest {

    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private JwtRevocationService jwtRevocationService;
    @MockitoBean
    private BearerTokenAuthenticationToken authentication;
    @MockitoBean
    private Jwt jwt;

    @Autowired
    private CustomJwtAuthenticationProvider provider;

    @Test
    public void testAuthenticate_validToken() {
        String token = "token";
        when(authentication.getPrincipal()).thenReturn(token);
        when(authentication.getToken()).thenReturn(token);
        when(jwtRevocationService.isRevoked(token)).thenReturn(false);
        when(jwtDecoder.decode(token)).thenReturn(jwt);
        provider.authenticate(authentication);
    }

    @Test
    public void testAuthenticate_revokedToken() {
        String token = "token";
        when(authentication.getPrincipal()).thenReturn(token);
        when(jwtRevocationService.isRevoked(token)).thenReturn(true);
        assertThrows(TokenRevokedException.class, () -> provider.authenticate(authentication));
        verify(jwtDecoder, never()).decode(token);
    }

    @Test
    public void testSupports() {
        assertTrue(provider.supports(BearerTokenAuthenticationToken.class));
        assertFalse(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

}
