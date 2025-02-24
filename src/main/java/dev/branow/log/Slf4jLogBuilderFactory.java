package dev.branow.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Slf4jLogBuilderFactory implements LogBuilderFactory {

    @Override
    public LogBuilder newLogBuilder(Level level, String source, String operation) {
        var slf4jLevel = toSlf4jLevel(level);
        var logger = log.atLevel(slf4jLevel);
        return new Slf4jLogBuilder(logger, source, operation);
    }

    public static org.slf4j.event.Level toSlf4jLevel(Level level) {
        if (level == null) return org.slf4j.event.Level.INFO; // Default level

        return switch (level) {
            case ERROR -> org.slf4j.event.Level.ERROR;
            case WARN -> org.slf4j.event.Level.WARN;
            case INFO -> org.slf4j.event.Level.INFO;
            case DEBUG -> org.slf4j.event.Level.DEBUG;
            case TRACE -> org.slf4j.event.Level.TRACE;
        };
    }

}
