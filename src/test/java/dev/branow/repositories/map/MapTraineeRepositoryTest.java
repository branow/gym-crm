package dev.branow.repositories.map;

import dev.branow.config.Config;
import dev.branow.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(Config.class)
public class MapTraineeRepositoryTest {

    @Autowired
    private MapTraineeRepository repository;

    @Test
    public void testGetId() {
        var expected = 99L;
        var trainee = new Trainee();
        trainee.setUserId(expected);
        var actual = repository.getId(trainee);
        assertEquals(expected, actual);
    }

    @Test
    public void testSetId() {
        var expected = 99L;
        var trainee = new Trainee();
        repository.setId(expected, trainee);
        var actual = trainee.getUserId();
        assertEquals(expected, actual);
    }

}
