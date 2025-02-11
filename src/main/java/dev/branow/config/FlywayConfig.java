package dev.branow.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(
            @Value("${jakarta.persistence.jdbc.url}") String url,
            @Value("${jakarta.persistence.jdbc.driver-class-name}") String driver,
            @Value("${jakarta.persistence.jdbc.user}") String username,
            @Value("${jakarta.persistence.jdbc.password}") String password
    ) {
        var flyway = Flyway.configure()
                .driver(driver)
                .dataSource(url, username, password)
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        return flyway;
    }

}
