package dev.branow.monitoring.health;

import dev.branow.repositories.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component("plannedTrainings")
@RequiredArgsConstructor
public class PlannedTrainingsIndicator implements HealthIndicator {

    private final TrainingRepository repository;

    @Override
    public Health health() {
        var info = check();
        return Optional.of(info.future)
                .filter(count -> count > 0)
                .map(_ -> Health.up()
                        .withDetail("status", "Trainings are scheduled"))
                .orElse(Health.down()
                        .withDetail("status", "There are no planned trainings"))
                .withDetail("inFuture", info.future)
                .withDetail("inPresent", info.present)
                .withDetail("inPast", info.past)
                .build();
    }

    private TrainingsInfo check() {
        var trainings = repository.findAll();
        var now = LocalDate.now();
        int past = (int) trainings.stream().filter(training -> training.getDate().isBefore(now)).count();
        int present = (int) trainings.stream().filter(training -> training.getDate().isEqual(now)).count();
        int future = (int) trainings.stream().filter(training -> training.getDate().isAfter(now)).count();
        return new TrainingsInfo(future, present, past);
    }

    private record TrainingsInfo(int future, int present, int past) {}

}
