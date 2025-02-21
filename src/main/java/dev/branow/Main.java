package dev.branow;

import dev.branow.config.Config;
import dev.branow.utils.WebServerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@Slf4j
public class Main {

    public static void main(String[] args) throws LifecycleException {
        try (var context = new AnnotationConfigWebApplicationContext()) {
            context.register(Config.class);

            var tomcat = new WebServerFactory().getTomcatServer(context);
            tomcat.start();
            log.info("Tomcat server started on port {}", tomcat.getConnector().getPort());
            tomcat.getServer().await();
        }
    }

}
