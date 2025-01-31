package dev.branow.repositories.map;

import dev.branow.config.Config;
import dev.branow.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(Config.class)
public class MapTrainerRepositoryTest {

    @Autowired
    private MapTrainerRepository repository;

    @Test
    public void testGetId() {
        var expected = 99L;
        var trainer = new Trainer();
        trainer.setUserId(expected);
        var actual = repository.getId(trainer);
        assertEquals(expected, actual);
    }

    @Test
    public void testSetId() {
        var expected = 99L;
        var trainer = new Trainer();
        repository.setId(expected, trainer);
        var actual = trainer.getUserId();
        assertEquals(expected, actual);
    }

}
