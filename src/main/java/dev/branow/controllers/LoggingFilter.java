package dev.branow.controllers;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var uuid = UUID.randomUUID();
        request.setAttribute("uuid", uuid);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        logRequest(request, uuid);

        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            logResponse(responseWrapper, uuid);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequest request, UUID uuid) {
        log.info("{}: request: {} {}", uuid, request.getMethod(), request.getRequestURI());
    }

    private void logResponse(ContentCachingResponseWrapper response, UUID uuid) {
        int status = response.getStatus();
        String responseBody = getResponseBody(response);

        if (status >= 500) {
            log.error("{}: response: {} body: {}", uuid, response.getStatus(), responseBody);
        } else if (status >= 400) {
            log.warn("{}: response: {} body: {}", uuid, response.getStatus(), responseBody);
        } else {
            log.info("{}: response: {}", uuid, response.getStatus());
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] body = response.getContentAsByteArray();
        return body.length > 0 ? new String(body) : "[empty]";
    }

}
