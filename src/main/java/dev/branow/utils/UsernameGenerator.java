package dev.branow.utils;

import dev.branow.model.User;

import java.util.stream.Stream;

public interface UsernameGenerator {
    <T extends User> String generate(T user, Stream<T> users);
}
