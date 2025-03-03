package dev.branow;

import dev.branow.config.PersistenceConfig;
import dev.branow.model.*;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(PersistenceConfig.class)
@EnableTransactionManagement
public class TestDBConfig {

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return new org.hibernate.cfg.Configuration()
                .setProperty(AvailableSettings.JAKARTA_JDBC_URL, DBTest.postgres.getJdbcUrl())
                .setProperty(AvailableSettings.JAKARTA_JDBC_USER, DBTest.postgres.getUsername())
                .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, DBTest.postgres.getPassword())
                .setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, DBTest.postgres.getDriverClassName())
                .setPhysicalNamingStrategy(new SnakePhysicalNamingStrategy())
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

    private static class SnakePhysicalNamingStrategy implements PhysicalNamingStrategy {

        @Override
        public Identifier toPhysicalCatalogName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
            return apply(logicalName);
        }

        @Override
        public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
            return apply(logicalName);
        }

        @Override
        public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
            return apply(logicalName);
        }

        @Override
        public Identifier toPhysicalSequenceName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
            return apply(logicalName);
        }

        @Override
        public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
            return apply(logicalName);
        }

        private Identifier apply(Identifier name) {
            if (name == null) return null;
            String newName = name.getText()
                    .replaceAll("([a-z])([A-Z])", "$1_$2")
                    .toLowerCase();
            return Identifier.toIdentifier(newName);
        }
    }

}
