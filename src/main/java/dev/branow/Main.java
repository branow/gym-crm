package dev.branow;

import dev.branow.cli.App;
import dev.branow.config.Config;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(Config.class)) {
            var app = context.getBean(App.class);
            app.start();
        }
    }
}
