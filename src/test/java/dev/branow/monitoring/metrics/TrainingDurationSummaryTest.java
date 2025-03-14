package dev.branow.monitoring.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig({TrainingDurationSummary.class, SimpleMeterRegistry.class})
public class TrainingDurationSummaryTest {

    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private TrainingDurationSummary summary;

    @Test
    public void test() {
        summary.recordTrainingDuration(20);
        summary.recordTrainingDuration(40);
        var actualSummary = meterRegistry.summary("gym.training.duration.minutes");
        assertEquals(2, actualSummary.count());
        assertEquals(40, actualSummary.max());
        assertEquals(60, actualSummary.totalAmount());
    }

}
