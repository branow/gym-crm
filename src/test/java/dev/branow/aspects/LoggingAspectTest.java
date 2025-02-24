package dev.branow.aspects;

import dev.branow.annotations.Log;
import dev.branow.log.Level;
import dev.branow.log.LogBuilder;
import dev.branow.log.LogBuilderFactory;
import dev.branow.log.UUIDProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ LoggingAspect.class, LoggingAspectTest.Config.class })
public class LoggingAspectTest {

    @MockitoBean
    private LogBuilderFactory logBuilderFactory;
    @Mock
    private LogBuilder logBuilder;

    @Autowired
    private LoggingAspect loggingAspect;
    @Autowired
    private Math math;

    @Test
    public void testLogExecution_successfulPath() {
        var signature = Config.uuid + ": int dev.branow.aspects.LoggingAspectTest$Math.divide(int,int)";
        var operation = "dividing 6 / 2";
        when(logBuilderFactory.newLogBuilder(Level.DEBUG, signature, operation)).thenReturn(logBuilder);

        assertEquals(3, math.divide(6, 2));

        verify(logBuilder, times(1)).started();
        verify(logBuilder, times(1)).finished();
    }

    @Test
    public void testLogExecution_failedPath() {
        var signature = Config.uuid + ": int dev.branow.aspects.LoggingAspectTest$Math.divide(int,int)";
        var operation = "dividing 6 / 0";
        when(logBuilderFactory.newLogBuilder(Level.DEBUG, signature, operation)).thenReturn(logBuilder);

        assertThrows(ArithmeticException.class, () -> math.divide(6, 0));

        verify(logBuilder, times(1)).started();
        verify(logBuilder, times(1)).failed(any(ArithmeticException.class));
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class Config {
        public static final UUID uuid = UUID.randomUUID();
        @Bean
        public Math math() {
            return new Math();
        }
        @Bean
        public UUIDProvider uuid() {
            return () -> uuid;
        }
    }

    public static class Math {
        @Log(value = "dividing %0 / %1", level = Level.DEBUG)
        public int divide(int a, int b) {
            return a / b;
        }
    }

}