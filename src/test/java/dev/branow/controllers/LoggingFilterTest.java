package dev.branow.controllers;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggingFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    private final LoggingFilter filter = new LoggingFilter();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testDoFilterInternal_request() throws IOException, ServletException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/foo");
        filter.doFilterInternal(request, response, chain);

        var requestPattern = String.format("%s: request: %s \\%s", getCommonPattern("INFO"), request.getMethod(), request.getRequestURI());
        testLog(requestPattern, 0);
        verify(request, times(1)).setAttribute(eq("uuid"), any(UUID.class));
    }

    @Test
    public void testDoFilterInternal_response200() throws IOException, ServletException {
        when(response.getStatus()).thenReturn(200);
        filter.doFilterInternal(request, response, chain);

        var responsePattern = String.format("%s: response: %s", getCommonPattern("INFO"), response.getStatus());
        testLog(responsePattern, 1);
    }

    @Test
    public void testDoFilterInternal_response400AndEmptyBody() throws IOException, ServletException {
        when(response.getStatus()).thenReturn(400);

        filter.doFilterInternal(request, response, chain);

        var responsePattern = String.format("%s: response: %s body: %s", getCommonPattern("WARN"), response.getStatus(), "\\[empty\\]");
        testLog(responsePattern, 1);
    }

    @Test
    public void testDoFilterInternal_response400() throws IOException, ServletException {
        var errorMessage = "Very nasty error message";
        when(response.getStatus()).thenReturn(400);

        var servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        doAnswer((invocation) -> {
            var wrapper = invocation.getArgument(1, HttpServletResponse.class);
            wrapper.getWriter().print(errorMessage);
            return null;
        }).when(chain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

        filter.doFilterInternal(request, response, chain);

        var responsePattern = String.format("%s: response: %s body: %s", getCommonPattern("WARN"), response.getStatus(), errorMessage);
        testLog(responsePattern, 1);
    }

    @Test
    public void testDoFilterInternal_response500() throws IOException, ServletException {
        var errorMessage = "Very nasty error message";
        when(response.getStatus()).thenReturn(500);

        var servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        doAnswer((invocation) -> {
            var wrapper = invocation.getArgument(1, HttpServletResponse.class);
            wrapper.getWriter().print(errorMessage);
            return null;
        }).when(chain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

        filter.doFilterInternal(request, response, chain);

        var responsePattern = String.format("%s: response: %s body: %s", getCommonPattern("ERROR"), response.getStatus(), errorMessage)
                .replace("ERROR  ", "ERROR ");
        testLog(responsePattern, 1);
    }


    private void testLog(String pattern, int i) {
        String actualResponse = outContent.toString().split("\n")[i];
        System.setOut(originalOut);
        System.out.println(pattern);
        System.out.println(actualResponse);
        assertTrue(actualResponse.matches(pattern));
    }

    private static String getCommonPattern(String logLevel) {
        return "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\[main\\] " +  logLevel + "  d\\.branow\\.controllers\\.LoggingFilter - [0-9a-f-]+";
    }

}
