package dev.branow.aspects;

import dev.branow.annotations.Authenticate;
import dev.branow.auth.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ AuthenticationAspect.class, AuthenticationAspectTest.Config.class })
public class AuthenticationAspectTest {

    @MockitoBean
    private CredentialsProvider credentialsProvider;
    @MockitoBean
    private AuthenticationProvider authenticationProvider;
    @MockitoBean
    private AuthenticationContext authenticationContext;
    @Mock
    private Credentials credentials;

    @Autowired
    private Math math;

    @Test
    public void testAuthenticate_unauthenticatedUser() {
        when(authenticationContext.isAuthenticated()).thenReturn(Boolean.FALSE);
        when(credentialsProvider.getCredentials()).thenReturn(credentials);

        assertEquals(4, math.squire(2));

        verify(authenticationProvider, times(1)).authenticate(credentials);
        verify(authenticationContext, times(1)).expire();
    }

    @Test
    public void testAuthenticate_authenticatedUser() {
        when(authenticationContext.isAuthenticated()).thenReturn(Boolean.TRUE);
        assertEquals(4, math.squire(2));
        verify(authenticationContext, times(0)).expire();
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class Config {
        @Bean
        public Math math() {
            return new Math();
        }
    }


    public static class Math {

        @Authenticate
        public int squire(int a) {
            return a * a;
        }

    }
}
