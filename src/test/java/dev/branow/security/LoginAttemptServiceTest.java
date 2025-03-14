package dev.branow.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(LoginAttemptService.class)
public class LoginAttemptServiceTest {

    @Autowired
    private LoginAttemptService service;

    @Test
    public void test() {
        var key = "key";
        var key2 = "key2";
        service.recordFailure(key);
        service.recordFailure(key);
        assertFalse(service.isBlocked(key));
        service.recordFailure(key);
        assertTrue(service.isBlocked(key));
        service.recordSuccess(key);
        assertFalse(service.isBlocked(key));
        service.recordFailure(key2);
        assertFalse(service.isBlocked(key));
        service.recordFailure(key);
        service.recordFailure(key);
        assertFalse(service.isBlocked(key));
    }


}
