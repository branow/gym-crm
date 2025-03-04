package dev.branow.monitoring.health;

import dev.branow.repositories.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("trainingTypeAvailability")
@RequiredArgsConstructor
public class TrainingTypeAvailabilityIndicator implements HealthIndicator {

    private final TrainingTypeRepository repository;

    @Override
    public Health health() {
        int count = repository.findAll().size();
        return Optional.of(count)
                .filter(c -> c > 0)
                .map(_ -> Health.up()
                        .withDetail("status", "Training types are available"))
                .orElse(Health.down()
                        .withDetail("status", "No training types found"))
                .withDetail("count", count)
                .build();
    }

}
