package dev.branow;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDBConfig {

    @Bean
    public MockDB mockDB() {
        return MockDB.getInstance();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(MockDB mockDB) {
        mockDB.initialize();
        return mockDB.connect();
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

}
