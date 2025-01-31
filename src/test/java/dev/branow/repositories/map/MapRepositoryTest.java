package dev.branow.repositories.map;

import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Reference;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MapRepositoryTest {

    private static final String KEY = "test-storage";
    private static final Reference<Map<Long, TestEntity>> REFERENCE = new Reference<>() {};

    @Mock
    private KeyValueStorage storage;
    @Mock
    private IdGenerator<Long> idGenerator;

    private TestMapRepository repo;

    @BeforeEach
    void setUp() {
        repo = new TestMapRepository(storage, idGenerator);
    }

    @Test
    public void testInit_withoutData_initEmptyMap() {
        var expected = new HashMap<>();
        when(storage.get(KEY, REFERENCE)).thenReturn(Optional.empty());
        repo.init();
        var actual = repo.map;
        assertEquals(expected, actual);
        verify(storage, times(1)).get(KEY, REFERENCE);
    }

    @Test
    public void testInit_withData_initMap() {
        var expected = getMap();
        when(storage.get(KEY, REFERENCE)).thenReturn(Optional.of(expected));
        repo.init();
        var actual = repo.map;
        assertEquals(expected, actual);
        verify(storage, times(1)).get(KEY, REFERENCE);
    }

    @Test
    public void testDestroy() {
        var expected = getMap();
        repo.map = expected;
        repo.destroy();
        verify(storage, times(1)).save(KEY, expected);
    }

    @Test
    public void testFindAll() {
        repo.map = getMap();
        var expected = repo.map.values().stream().toList();
        var actual = repo.findAll().toList();
        assertEquals(expected, actual);
    }

    @Test
    public void testFindIdAll() {
        repo.map = getMap();
        var expected = repo.map.keySet().stream().toList();
        var actual = repo.findIdAll().toList();
        assertEquals(expected, actual);
    }

    @Test
    public void testFindById() {
        repo.map = getMap();
        var id = repo.map.keySet().iterator().next();
        var expected = Optional.ofNullable(repo.map.get(id));
        var actual = repo.findById(id);
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteById() {
        repo.map = getMap();
        var id = repo.map.keySet().iterator().next();
        repo.deleteById(id);
        assertFalse(repo.map.containsKey(id));
    }

    @Test
    public void testCreate() {
        var id = -1L;
        when(idGenerator.generate(any())).thenReturn(id);
        var entity = TestEntity.builder().name("x").build();
        var expected = new TestEntity(id, entity.getName());
        repo.map = getMap();
        var actual1 = repo.create(entity);
        var actual2 = repo.map.get(id);
        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
    }

    @Test
    public void testUpdate_idPresent() {
        repo.map = getMap();
        var id = repo.map.keySet().iterator().next();
        var expected = new TestEntity(id, "x");
        var actual1 = repo.update(expected);
        var actual2 = repo.map.get(id);
        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
    }

    @Test
    public void testUpdate_idAbsent_throwException() {
        repo.map = getMap();
        var entity = new TestEntity(-1L, "x");
        assertThrows(IllegalArgumentException.class, () -> repo.update(entity));
    }

    @Test
    public void testDeleteAllByCondition() {
        Predicate<Map.Entry<Long, TestEntity>> condition = (entry) -> entry.getValue().id < 3;

        var expected = getMap().entrySet().stream()
                .filter((e) -> !condition.test(e))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        repo.map = getMap();
        repo.deleteAllByCondition(condition);
        var actual = repo.map;
        assertEquals(expected, actual);
    }

    private static Map<Long, TestEntity> getMap() {
        Map<Long, TestEntity> map = new HashMap<>();
        map.put(1L, new TestEntity(1L, "a"));
        map.put(2L, new TestEntity(2L, "b"));
        map.put(3L, new TestEntity(3L, "c"));
        return map;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    static class TestEntity {
        private Long id;
        private String name;
    }

    static class TestMapRepository extends MapRepository<Long, TestEntity> {

        public TestMapRepository(KeyValueStorage storage, IdGenerator<Long> idGenerator) {
            super(KEY, REFERENCE, storage, idGenerator);
        }

        @Override
        protected Long getId(TestEntity value) {
            return value.getId();
        }

        @Override
        protected void setId(Long id, TestEntity value) {
            value.setId(id);
        }
    }

}

