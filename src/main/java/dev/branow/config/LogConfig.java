package dev.branow.config;

import dev.branow.log.UUIDProvider;
import dev.branow.log.UUIDProviderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfig {

    @Bean
    public UUIDProvider uuidProvider(UUIDProviderFactory factory) {
        return factory.createWebUUIDProvider();
    }

}
