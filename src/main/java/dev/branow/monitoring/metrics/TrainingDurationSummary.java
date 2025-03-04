package dev.branow.monitoring.metrics;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainingDurationSummary {

    private final DistributionSummary summary;

    public TrainingDurationSummary(MeterRegistry registry) {
        this.summary = DistributionSummary.builder("gym.training.duration.minutes")
                .description("Duration of gym trainings in minutes")
                .baseUnit("minutes")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram(true)
                .maximumExpectedValue(60 * 8.)
                .minimumExpectedValue(5.)
                .register(registry);
    }

    public void recordTrainingDuration(int duration) {
        summary.record(duration);
    }

}
