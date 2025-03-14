package dev.branow.monitoring.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig({ TrainerRegistrationCounter.class, SimpleMeterRegistry.class })
public class TrainerRegistrationCounterTest {

    @Autowired
    private SimpleMeterRegistry meterRegistry;
    @Autowired
    private TrainerRegistrationCounter counter;

    @Test
    public void test() {
        counter.increment();
        counter.increment();
        var actualCounter = meterRegistry.counter("trainer.registrations", "trainer", "registration");
        assertEquals(2, actualCounter.count());
    }

}
