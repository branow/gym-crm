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
import java.time.LocalDate;

import static dev.branow.EntityManagerUtils.*;
import static dev.branow.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestDBConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTraining {

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
        var training = Training.builder()
                .id(1L)
                .name("Full Body Strength")
                .date(LocalDate.of(2024, 2, 10))
                .duration(60)
                .type(type)
                .build();
        var actual = manager.find(Training.class, training.getId());
        assertEquals(training, actual);
    }

    @Test
    @Order(1)
    public void testPersist_withValidTraining_persistTraining() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        var expectedId = lastId(manager, Training.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(training);
        expected.setId(expectedId);

        persist(manager, training);
        assertEquals(expectedId, training.getId());
        assertEquals(expected, training);
    }

    @Test
    public void testPersist_withoutName_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setName(null);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withTooLongName_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var validTraining = nextTraining(type, trainee, trainer);
        validTraining.setName("a".repeat(100));
        persist(manager, validTraining);

        var invalidTraining = nextTraining(type, trainee, trainer);
        invalidTraining.setName("a".repeat(101));
        assertThrows(DataException.class,
                () -> persist(manager, invalidTraining));
    }

    @Test
    public void testPersist_withoutDate_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDate(null);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }


    @Test
    public void testPersist_withoutDuration_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDuration(null);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withNegativeDuration_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDuration(-1);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withZeroDuration_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDuration(0);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withoutTrainingType_throwsException() {
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(null, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withoutTrainee_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, null, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withoutTrainer_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var training = nextTraining(type, trainee, null);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withNewTrainingType_throwsException() {
        var type = nextTrainingType();
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withNewTrainee_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = nextTrainee(null);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testPersist_withNewTrainer_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = nextTrainer(type, null);
        var training = nextTraining(type, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> persist(manager, training));
    }

    @Test
    public void testRemove() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        persist(manager, training);
        var id = training.getId();

        manager.remove(training);
        assertNotNull(manager.find(Trainer.class, 4L));
        assertNotNull(manager.find(Trainee.class, 1L));
        assertNotNull(manager.find(TrainingType.class, 1L));
        assertNull(manager.find(Training.class, id));
    }

}
