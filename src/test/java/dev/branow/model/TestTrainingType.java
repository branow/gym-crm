package dev.branow.model;

import dev.branow.DBTest;
import dev.branow.TestDataFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.*;

import static dev.branow.TestDataFactory.nextTrainingType;
import static org.junit.jupiter.api.Assertions.*;

public class TestTrainingType extends DBTest {

    @Test
    public void testFind() {
        var type = TrainingType.builder()
                .id(1L)
                .name("Strength Training")
                .build();
        var actual = manager.find(TrainingType.class, type.getId());
        assertEquals(type, actual);
    }

    @Test
    @Order(1)
    public void testPersist_validTrainingType_persistTrainingType() {
        var type = nextTrainingType();
        var expectedId = manager.lastId(TrainingType.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(type);
        expected.setId(expectedId);

        manager.persist(type);
        assertEquals(expectedId, type.getId());
        assertEquals(expected, type);
    }

    @Test
    public void testPersist_withoutName_throwsException() {
        var type = nextTrainingType();
        type.setName(null);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(type));
    }

    @Test
    public void testPersist_withTooLongName_throwsException() {
        var validType = nextTrainingType();
        validType.setName("a".repeat(100));
        manager.persist(validType);

        var invalidType = nextTrainingType();
        invalidType.setName("a".repeat(101));
        assertThrows(DataException.class,
                () -> manager.persist(invalidType));
    }

    @Test
    public void testRemove_noReference_removeTrainingType() {
        var type = nextTrainingType();
        manager.persist(type);
        assertNotNull(manager.find(TrainingType.class, type.getId()));
        manager.remove(type);
        assertNull(manager.find(TrainingType.class, type.getId()));
    }

    @Test
    public void testRemove_hasReference_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        assertThrows(ConstraintViolationException.class, () -> manager.remove(type));
    }


}
