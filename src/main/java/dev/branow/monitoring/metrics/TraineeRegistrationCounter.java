package dev.branow.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TraineeRegistrationCounter {

    private final Counter traineeRegistrations;

    public TraineeRegistrationCounter(MeterRegistry registry) {
        traineeRegistrations = Counter.builder("trainee.registrations")
                .description("Counter for tracking Trainee registrations")
                .tags("trainee", "registration")
                .register(registry);
    }

    public void increment() {
        traineeRegistrations.increment();
    }

}
