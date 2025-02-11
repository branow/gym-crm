package dev.branow.model;

import dev.branow.MockDB;
import dev.branow.TestDBConfig;
import dev.branow.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.nio.file.Path;

import static dev.branow.EntityManagerUtils.*;
import static dev.branow.TestDataFactory.nextTrainingType;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestDBConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTrainingType {

    @Autowired
    private EntityManager manager;

    @AfterEach
    public void cleanUp() {
        clean(manager);
    }

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
        var expectedId = lastId(manager, TrainingType.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(type);
        expected.setId(expectedId);

        persist(manager, type);
        assertEquals(expectedId, type.getId());
        assertEquals(expected, type);
    }

    @Test
    public void testPersist_withoutName_throwsException() {
        var type = nextTrainingType();
        type.setName(null);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, type));
    }

    @Test
    public void testPersist_withTooLongName_throwsException() {
        var validType = nextTrainingType();
        validType.setName("a".repeat(100));
        persist(manager, validType);

        var invalidType = nextTrainingType();
        invalidType.setName("a".repeat(101));
        assertThrows(DataException.class,
                () -> persist(manager, invalidType));
    }

    @Test
    public void testRemove_noReference_removeTrainingType() {
        var type = nextTrainingType();
        persist(manager, type);
        assertNotNull(manager.find(TrainingType.class, type.getId()));
        remove(manager, type);
        assertNull(manager.find(TrainingType.class, type.getId()));
    }

    @Test
    public void testRemove_hasReference_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        assertThrows(ConstraintViolationException.class, () -> remove(manager, type));
    }


}
