package dev.branow.controllers;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class RestUtils {

    public static MockHttpServletRequestBuilder rest(MockHttpServletRequestBuilder builder) {
        return builder.characterEncoding("UTF-8")
                .header("Content-Type", "application/json");
    }

}
