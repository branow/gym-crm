package dev.branow.repositories.map;

import dev.branow.repositories.Repository;
import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Reference;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public abstract class MapRepository<K, V> implements Repository<K, V> {

    private final String key;
    private final Reference<Map<K, V>> reference;
    private final KeyValueStorage storage;
    private final IdGenerator<K> idGenerator;
    protected Map<K, V> map;

    @PostConstruct
    public void init() {
        log.debug("Loading data for key: {}", key);
        map = storage.get(key, reference).orElse(new HashMap<>());
        log.info("Data successfully loaded for key: {}", key);
    }

    @PreDestroy
    public void destroy() {
        log.debug("Saving data for key: {}", key);
        storage.save(key, map);
        log.info("Data successfully saved for key: {}", key);
    }

    protected abstract K getId(V value);
    protected abstract void setId(K id, V value);

    @Override
    public Stream<V> findAll() {
        log.debug("Finding all data for key: {}", key);
        return map.values().stream();
    }

    @Override
    public Stream<K> findIdAll() {
        log.debug("Finding all IDs for key: {}", key);
        return map.keySet().stream();
    }

    @Override
    public Optional<V> findById(K id) {
        log.debug("Finding entity by id: {}", id);
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public void deleteById(K id) {
        log.debug("Deleting entity by id: {}", id);
        map.remove(id);
    }

    protected void deleteAllByCondition(Predicate<Map.Entry<K, V>> condition) {
        var keysToRemove = map.entrySet().stream()
                .filter(condition)
                .map(Map.Entry::getKey)
                .toList();
        keysToRemove.forEach(this::deleteById);
        log.debug("Deleted {} entities by condition", keysToRemove.size());
    }

    @Override
    public V create(V value) {
        log.debug("Creating new entity: {}", value);
        var id = idGenerator.generate(this);
        setId(id, value);
        map.put(id, value);
        log.debug("Entity created with id: {}", id);
        return value;
    }

    @Override
    public V update(V value) {
        var id = getId(value);
        if (!map.containsKey(id)) {
            log.warn("Attempted to update non-existing entity with id: {}", id);
            throw new IllegalArgumentException("Entity does not exist: id=" + id);
        }
        log.debug("Updating entity with id: {}", id);
        map.put(id, value);
        log.debug("Entity updated with id: {}", id);
        return value;
    }

}