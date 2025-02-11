package dev.branow.log;

import org.slf4j.spi.LoggingEventBuilder;

public record Slf4jLogBuilder(
        LoggingEventBuilder logger,
        String source,
        String operation
) implements LogBuilder {

    enum State {
        START("Started"),
        FINISHED("Finished"),
        FAILED("Failed");

        private final String value;

        State(String value) {
            this.value = value;
        }
    }

    public void started() {
        log(State.START, "");
    }

    public void finished() {
        log(State.FINISHED, "");
    }

    public void failed(Throwable throwable) {
        log(State.FAILED, throwable.getMessage());
    }

    private void log(State state, String info) {
        var message = buildMessage(state, info);
        logger.log(message);
    }

    private String buildMessage(State state, String info) {
        return String.format("%s: %s %s: %s", source, state.value, operation, info);
    }

}
