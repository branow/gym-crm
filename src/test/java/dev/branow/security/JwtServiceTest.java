package dev.branow.security;

import dev.branow.dtos.service.UserDetailsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(JwtService.class)
public class JwtServiceTest {

    @MockitoBean
    private JwtEncoder jwtEncoder;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService service;

    @Test
    public void testGenerate() {
        var username = "username";
        var token = "token";
        var userDetails = UserDetailsDto.builder().username(username).build();
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        var jwt = mock(Jwt.class);
        doAnswer((args) -> {
            JwtEncoderParameters params = args.getArgument(0);
            assertEquals("self", params.getClaims().getClaim("iss"));
            assertEquals("username", params.getClaims().getClaim("sub"));
            return jwt;
        }).when(jwtEncoder).encode(any());
        when(jwt.getTokenValue()).thenReturn(token);

        var actualToken = service.generate(new UsernamePasswordAuthenticationToken(username, ""));
        assertEquals(token, actualToken);
    }

}
