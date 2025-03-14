package dev.branow.config;

import dev.branow.security.CustomJwtAuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(WebSecurityConfig.class)
public class WebSecurityConfigTest {

    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private CustomJwtAuthenticationProvider customJwtAuthenticationProvider;

    @Autowired
    private SecurityFilterChain securityFilterChain;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    public void test() {
        assertNotNull(securityFilterChain);
        assertNotNull(passwordEncoder);
        assertNotNull(authenticationManager);
    }

}
