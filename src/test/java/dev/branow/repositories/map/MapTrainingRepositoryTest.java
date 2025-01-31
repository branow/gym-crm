package dev.branow.repositories.map;

import dev.branow.model.Training;
import dev.branow.storage.KeyValueStorage;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = MapTrainingRepository.class)
public class MapTrainingRepositoryTest {

    @MockitoBean
    private KeyValueStorage keyValueStorage;
    @MockitoBean
    private IdGenerator<Long> idGenerator;

    @Autowired
    private MapTrainingRepository repository;

    @Test
    public void testGetId() {
        var expected = 99L;
        var training = new Training();
        training.setTrainingId(expected);
        var actual = repository.getId(training);
        assertEquals(expected, actual);
    }

    @Test
    public void testSetId() {
        var expected = 99L;
        var training = new Training();
        repository.setId(expected, training);
        var actual = training.getTrainingId();
        assertEquals(expected, actual);
    }

}
