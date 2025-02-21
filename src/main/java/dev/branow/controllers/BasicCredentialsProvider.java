package dev.branow.controllers;

import dev.branow.auth.Credentials;
import dev.branow.auth.CredentialsProvider;
import dev.branow.auth.SimpleCredentials;
import dev.branow.exceptions.BadCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;
import java.util.Optional;

@Component
public class BasicCredentialsProvider implements CredentialsProvider {

    @Override
    public Credentials getCredentials() {
        var request = getRequest();
        var authorizationHeader = getAuthorizationHeader(request);
        String[] credentials = decodeBasicAuth(authorizationHeader);
        return new SimpleCredentials(credentials[0], credentials[1]);
    }

    private HttpServletRequest getRequest() {
        return  Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(attributes -> ((ServletRequestAttributes) attributes).getRequest())
                .orElseThrow(() -> new IllegalStateException("No request attributes found"));
    }

    private String getAuthorizationHeader(HttpServletRequest request) {
        var prefix = "Basic ";
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith(prefix))
                .map(header -> header.substring(prefix.length()))
                .orElseThrow(BadCredentialsException::new);
    }

    private String[] decodeBasicAuth(String encodedCredentials) {
        try {
            var decode = new String(Base64.getDecoder().decode(encodedCredentials.trim()));
            var credentials = decode.split(":");
            if (credentials.length != 2) {
                throw new BadCredentialsException();
            }
            return credentials;
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(e);
        }
    }

}
