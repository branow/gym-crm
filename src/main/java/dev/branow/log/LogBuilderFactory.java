package dev.branow.log;

public interface LogBuilderFactory {
    LogBuilder newLogBuilder(Level level, String source, String operation);
}
