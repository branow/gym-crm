package dev.branow.aspects;

import dev.branow.annotations.Authorize;
import dev.branow.auth.AlwaysAllowAuthorizer;
import dev.branow.auth.AuthenticationContext;
import dev.branow.auth.Authorizer;
import dev.branow.auth.Credentials;
import dev.branow.exceptions.AccessDeniedException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ AuthorizationAspect.class, AuthorizationAspectTest.Config.class })
public class AuthorizationAspectTest {


    @MockitoBean
    private AuthenticationContext authenticationContext;
    @MockitoBean
    private IntegerAuthorizer authorizer;
    @MockitoBean
    private AlwaysAllowAuthorizer alwaysAllowAuthorizer;
    @Mock
    private Credentials credentials;

    @Autowired
    private Math math;

    @Test
    public void testAuthorize_unauthenticatedUser_throwsException() {
        when(authenticationContext.isAuthenticated()).thenReturn(Boolean.FALSE);
        assertThrows(AccessDeniedException.class, () -> math.squire(4));
    }

    @Test
    public void testAuthorize_withoutResource_throwsException() {
        when(authenticationContext.isAuthenticated()).thenReturn(Boolean.TRUE);
        assertThrows(IllegalArgumentException.class, () -> math.pi());
    }

    @Test
    public void testAuthorize_defaultAuthorizer_authorize() {
        when(authenticationContext.isAuthenticated()).thenReturn(Boolean.TRUE);
        when(authenticationContext.getCredentials()).thenReturn(credentials);
        assertEquals(4, math.root(2));
        verify(alwaysAllowAuthorizer, times(1)).authorize(2, credentials);
    }

    @Test
    public void testAuthorize_customAuthorizer_authorize() {
        when(authenticationContext.isAuthenticated()).thenReturn(Boolean.TRUE);
        when(authenticationContext.getCredentials()).thenReturn(credentials);
        assertEquals(4, math.squire(2));
        verify(authorizer, times(1)).authorize(2, credentials);
    }


    @Configuration
    @EnableAspectJAutoProxy
    public static class Config {
        @Bean
        public Math math() {
            return new Math();
        }
        @Bean
        public IntegerAuthorizer authorizer() {
            return (value, credentials) -> {};
        }
    }

    public interface IntegerAuthorizer extends Authorizer<Integer> { }

    public static class Math {
        @Authorize
        public double pi() {
            return 3.14;
        }
        @Authorize
        public int root(int a) {
            return a * a;
        }
        @Authorize(IntegerAuthorizer.class)
        public int squire(int a) {
            return a * a;
        }
    }

}
