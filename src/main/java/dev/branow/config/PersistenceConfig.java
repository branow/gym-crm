package dev.branow.config;

import dev.branow.model.*;
import dev.branow.repositories.TrainingRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
        basePackages = "dev.branow.repositories",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, value = TrainingRepository.class
        )
)
public class PersistenceConfig {

    @Bean
    public EntityManagerFactory entityManagerFactory(
            @Value("${jakarta.persistence.jdbc.url}") String url,
            @Value("${jakarta.persistence.jdbc.user}") String user,
            @Value("${jakarta.persistence.jdbc.password}") String password,
            PhysicalNamingStrategy physicalNamingStrategy
    ) {
        return new org.hibernate.cfg.Configuration()
                .setProperty(AvailableSettings.JAKARTA_JDBC_URL, url)
                .setProperty(AvailableSettings.JAKARTA_JDBC_USER, user)
                .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, password)
                .setPhysicalNamingStrategy(physicalNamingStrategy)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Trainee.class)
                .addAnnotatedClass(Trainer.class)
                .addAnnotatedClass(Training.class)
                .addAnnotatedClass(TrainingType.class)
                .buildSessionFactory();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
