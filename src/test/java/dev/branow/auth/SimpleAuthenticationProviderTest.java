package dev.branow.auth;

import dev.branow.exceptions.BadCredentialsException;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ SimpleAuthenticationContext.class, SimpleAuthenticationProvider.class })
public class SimpleAuthenticationProviderTest {

    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private SimpleAuthenticationContext context;
    @Autowired
    private SimpleAuthenticationProvider provider;

    @Test
    public void testAuthenticate_unknownUser() {
        var credentials = new SimpleCredentials("username", "password");
        when(userRepository.findByUsername(credentials.getUsername())).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> provider.authenticate(credentials));
        assertEquals(credentials, context.getCredentials());
        assertFalse(context.isAuthenticated());
    }

    @Test
    public void testAuthenticate_failedAuthentication() {
        var credentials = new SimpleCredentials("username", "password");
        var user = User.builder().password("another password").build();
        when(userRepository.findByUsername(credentials.getUsername())).thenReturn(Optional.of(user));
        assertThrows(BadCredentialsException.class, () -> provider.authenticate(credentials));
        assertEquals(credentials, context.getCredentials());
        assertFalse(context.isAuthenticated());
    }

    @Test
    public void testAuthenticate_successfulAuthentication() {
        var credentials = new SimpleCredentials("username", "password");
        var user = User.builder().password(credentials.getPassword()).build();
        when(userRepository.findByUsername(credentials.getUsername())).thenReturn(Optional.of(user));
        assertDoesNotThrow(() -> provider.authenticate(credentials));
        assertEquals(credentials, context.getCredentials());
        assertTrue(context.isAuthenticated());
    }

}









