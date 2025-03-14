package dev.branow.security;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtRevocationService {

    private final Map<String, Boolean> revokedTokens = new HashMap<>();

    public void revoke(String token) {
        revokedTokens.put(token, true);
    }

    public boolean isRevoked(String token) {
        return revokedTokens.get(token) != null;
    }

}
