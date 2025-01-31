package dev.branow.repositories;

import java.util.Optional;
import java.util.stream.Stream;

public interface Repository<ID, T> {
    T create(T trainee);
    Stream<T> findAll();
    Stream<ID> findIdAll();
    Optional<T> findById(ID traineeId);
    T update(T trainee);
    void deleteById(ID traineeId);
}
