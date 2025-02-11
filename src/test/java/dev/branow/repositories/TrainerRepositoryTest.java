package dev.branow.repositories;

import dev.branow.MockDB;
import dev.branow.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import static dev.branow.EntityManagerUtils.findAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(TrainerRepositoryTest.TestConfig.class)
public class TrainerRepositoryTest {

    @Autowired
    private EntityManager manager;

    @Autowired
    private TrainerRepository repository;

    @Test
    @Transactional
    public void testFindAll() {
        var trainers = findAll(manager, Trainer.class);
        var actual = repository.findAll();
        assertEquals(trainers, actual);
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(strings = {"John.Doe", "Bob.Brown", "James.Taylor", "Sophia.White"})
    public void testFindAllNotAssignedOnTraineeByTraineeUsername(String username) {
        var trainers = findAll(manager, Trainer.class);
        var expected = trainers.stream().filter(trainer ->
                trainer.getTrainings().isEmpty() ||
                trainer.getTrainings().stream().noneMatch(training -> training.getTrainee().getUsername().equals(username))
                )
                .toList();
        var actual = repository.findAllNotAssignedOnTraineeByTraineeUsername(username);
        assertEquals(expected, actual);
    }

    @Configuration
    @EnableJpaRepositories(
            basePackages = "dev.branow.repositories",
            excludeFilters = @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE, value = TrainingRepository.class
            )
    )
    public static class TestConfig {

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
        public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }

    }

}
