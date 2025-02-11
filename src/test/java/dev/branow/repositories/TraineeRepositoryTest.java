package dev.branow.repositories;

import dev.branow.MockDB;
import dev.branow.model.Trainee;
import dev.branow.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig(TraineeRepositoryTest.TestConfig.class)
public class TraineeRepositoryTest {

    @Autowired
    private EntityManager manager;

    @Autowired
    private TraineeRepository repository;

    @Test
    @Transactional
    public void testDelete() {
        var trainee = manager.find(Trainee.class, 1L);
        var trainingsIds = trainee.getTrainings().stream().map(Training::getId).toList();
        repository.deleteById(trainee.getId());
        trainingsIds.forEach(trainingId -> assertNull(manager.find(Training.class, trainingId)));
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
