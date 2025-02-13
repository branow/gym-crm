package dev.branow.log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.spi.LoggingEventBuilder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Slf4jLogBuilderTest {

    @Mock
    private LoggingEventBuilder logger;

    @Test
    public void testStarted() {
        String source = "source", operation = "operation";
        var expected = buildMessage(source, "Started", operation, "");
        new Slf4jLogBuilder(logger, source, operation).started();
        verify(logger, times(1)).log(expected);
    }

    @Test
    public void testFinished() {
        String source = "source", operation = "operation";
        var expected = buildMessage(source, "Finished", operation, "");
        new Slf4jLogBuilder(logger, source, operation).finished();
        verify(logger, times(1)).log(expected);
    }

    @Test
    public void testFailed() {
        var exception = new RuntimeException("exception message");
        String source = "source", operation = "operation";
        var expected = buildMessage(source, "Failed", operation, exception.getMessage());
        new Slf4jLogBuilder(logger, source, operation).failed(exception);
        verify(logger, times(1)).log(expected);
    }

    private String buildMessage(String source, String state, String operation, String info) {
        return String.format("%s: %s %s: %s", source, state, operation, info);
    }

}
