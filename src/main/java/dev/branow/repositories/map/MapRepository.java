package dev.branow.repositories.map;

import dev.branow.repositories.Repository;
import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Reference;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class MapRepository<K, V> implements Repository<K, V> {

    private final String key;
    private final Reference<Map<K, V>> reference;
    private final KeyValueStorage storage;
    private final IdGenerator<K> idGenerator;
    protected Map<K, V> map;

    @PostConstruct
    public void init() {
        map = storage.get(key, reference).orElse(new HashMap<>());
    }

    @PreDestroy
    public void destroy() {
        storage.save(key, map);
    }

    protected abstract K getId(V value);
    protected abstract void setId(K id, V value);

    @Override
    public Stream<V> findAll() {
        return map.values().stream();
    }

    @Override
    public Stream<K> findIdAll() {
        return map.keySet().stream();
    }

    @Override
    public Optional<V> findById(K id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public void deleteById(K id) {
        map.remove(id);
    }

    protected void deleteAllByCondition(Predicate<Map.Entry<K, V>> condition) {
        var keysToRemove = map.entrySet().stream()
                .filter(condition)
                .map(Map.Entry::getKey)
                .toList();
        keysToRemove.forEach(map::remove);
    }

    @Override
    public V create(V value) {
        var id = idGenerator.generate(this);
        setId(id, value);
        map.put(id, value);
        return value;
    }

    @Override
    public V update(V value) {
        var id = getId(value);
        if (!map.containsKey(id)) {
            throw new IllegalArgumentException("Entity does not exist: id=" + id);
        }
        map.put(id, value);
        return value;
    }

}