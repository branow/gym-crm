package dev.branow;

import dev.branow.config.FlywayConfig;
import dev.branow.config.PersistenceConfig;
import dev.branow.config.SnakePhysicalNamingStrategy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Configuration
@Import({
        SnakePhysicalNamingStrategy.class,
        PersistenceConfig.class,
        FlywayConfig.class,
})
@SpringJUnitConfig
public class TestDBConfig {}
