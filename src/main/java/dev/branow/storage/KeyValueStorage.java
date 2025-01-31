package dev.branow.storage;

import java.util.Optional;

public interface KeyValueStorage {
    <T> Optional<T> get(String key, Reference<T> reference);
    <T> void save(String key, T value);
}

