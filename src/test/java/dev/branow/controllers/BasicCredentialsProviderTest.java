package dev.branow.controllers;

import dev.branow.auth.SimpleCredentials;
import dev.branow.exceptions.BadCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(BasicCredentialsProvider.class)
@ExtendWith(MockitoExtension.class)
public class BasicCredentialsProviderTest {

    @Autowired
    private BasicCredentialsProvider provider;

    @Mock
    private HttpServletRequest request;


    @BeforeEach
    void setUp() {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    public void testGetCredentials_withoutAuthorizationHeader_throwException() {
        when(request.getHeader("Authorization")).thenReturn("");
        assertThrows(BadCredentialsException.class, () -> provider.getCredentials());
    }

    @Test
    public void testGetCredentials_invalidTypeAuthorizationHeader_throwException() {
        when(request.getHeader("Authorization")).thenReturn("Bearer fksdjfkjsahfslafh");
        assertThrows(BadCredentialsException.class, () -> provider.getCredentials());
    }

    @Test
    public void testGetCredentials_invalidBase64Encoding_throwException() {
        when(request.getHeader("Authorization")).thenReturn("Basic fksdjfkjsahfslafh");
        assertThrows(BadCredentialsException.class, () -> provider.getCredentials());
        when(request.getHeader("Authorization")).thenReturn("Basic aG9tZQ==");
        assertThrows(BadCredentialsException.class, () -> provider.getCredentials());
    }

    @Test
    public void testGetCredentials_validAuthorizationHeader_returnCredentials() {
        var credentials = new SimpleCredentials("username", "password");
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
        assertEquals(credentials, provider.getCredentials());
    }

}
