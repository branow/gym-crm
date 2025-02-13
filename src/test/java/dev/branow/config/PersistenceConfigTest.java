package dev.branow.config;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig({ PersistenceConfigTest.PropertiesConfig.class, SnakePhysicalNamingStrategy.class, PersistenceConfig.class })
public class PersistenceConfigTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    public void test() {
        assertNotNull(entityManagerFactory);
        assertNotNull(transactionManager);
    }

    @Configuration
    @PropertySource("classpath:application.properties")
    public static class PropertiesConfig {}

}
