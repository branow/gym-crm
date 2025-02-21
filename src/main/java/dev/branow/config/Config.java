package dev.branow.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("dev.branow")
@PropertySource("classpath:application.properties")
@EnableWebMvc
@EnableAspectJAutoProxy
public class Config {

}
