package dev.branow.aspects;

import dev.branow.annotations.Authorize;
import dev.branow.security.authorization.AlwaysAllowAuthorizer;
import dev.branow.security.authorization.Authorizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ AuthorizationAspect.class, AuthorizationAspectTest.Config.class })
public class AuthorizationAspectTest {

    @MockitoBean
    private Authentication authentication;
    @MockitoBean
    private SecurityContext securityContext;
    @MockitoBean
    private IntegerAuthorizer authorizer;
    @MockitoBean
    private AlwaysAllowAuthorizer alwaysAllowAuthorizer;

    @Autowired
    private Math math;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testAuthorize_unauthenticatedUser_throwsException() {
        when(authentication.isAuthenticated()).thenReturn(Boolean.FALSE);
        assertThrows(AccessDeniedException.class, () -> math.squire(4));
    }

    @Test
    public void testAuthorize_withoutResource_throwsException() {
        when(authentication.isAuthenticated()).thenReturn(Boolean.TRUE);
        assertThrows(IllegalArgumentException.class, () -> math.pi());
    }

    @Test
    public void testAuthorize_defaultAuthorizer_authorize() {
        when(authentication.isAuthenticated()).thenReturn(Boolean.TRUE);
        assertEquals(4, math.root(2));
        verify(alwaysAllowAuthorizer, times(1)).authorize(2, authentication);
    }

    @Test
    public void testAuthorize_customAuthorizer_authorize() {
        when(authentication.isAuthenticated()).thenReturn(Boolean.TRUE);
        assertEquals(4, math.squire(2));
        verify(authorizer, times(1)).authorize(2, authentication);
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
