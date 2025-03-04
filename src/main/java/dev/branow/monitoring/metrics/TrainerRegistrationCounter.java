package dev.branow.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainerRegistrationCounter {

    private final Counter traineeRegistrations;

    public TrainerRegistrationCounter(MeterRegistry registry) {
        traineeRegistrations = Counter.builder("trainer.registrations")
                .description("Counter for tracking Trainer registrations")
                .tags("trainer", "registration")
                .register(registry);
    }

    public void increment() {
        traineeRegistrations.increment();
    }

}
