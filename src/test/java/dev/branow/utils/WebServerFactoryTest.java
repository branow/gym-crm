package dev.branow.utils;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class WebServerFactoryTest {

    private final WebServerFactory factory = new WebServerFactory();

    @Test
    public void testGetTomcatServer() {
        var context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);

        assertDoesNotThrow(() -> {
            var tomcat = factory.getTomcatServer(context);
            tomcat.start();
            Thread.sleep(1000);
            tomcat.stop();
        });
    }

    @Configuration
    @EnableWebMvc
    public static class WebConfig {}

}
