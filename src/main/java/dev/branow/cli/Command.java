package dev.branow.cli;

import lombok.Builder;

import java.util.function.Function;

@Builder
public record Command(String key, String description, String usage, Function<String[], String> executor) {
    public String execute(String... args) {
        return executor.apply(args);
    }
}
