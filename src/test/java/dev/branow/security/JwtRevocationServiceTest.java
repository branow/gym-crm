package dev.branow.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(JwtRevocationService.class)
public class JwtRevocationServiceTest {

    @Autowired
    private JwtRevocationService service;

    @Test
    public void test() {
        var key1 = "key1";
        var key2 = "key2";
        assertFalse(service.isRevoked(key1));
        assertFalse(service.isRevoked(key2));
        service.revoke(key1);
        assertTrue(service.isRevoked(key1));
        assertFalse(service.isRevoked(key2));
    }

}
