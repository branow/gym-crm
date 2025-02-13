package dev.branow.model;

import dev.branow.DBTest;
import dev.branow.TestDataFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static dev.branow.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestTraining extends DBTest {

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
        var expectedId = manager.lastId(Training.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(training);
        expected.setId(expectedId);

        manager.persist(training);
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
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withTooLongName_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var validTraining = nextTraining(type, trainee, trainer);
        validTraining.setName("a".repeat(100));
        manager.persist(validTraining);

        var invalidTraining = nextTraining(type, trainee, trainer);
        invalidTraining.setName("a".repeat(101));
        assertThrows(DataException.class,
                () -> manager.persist(invalidTraining));
    }

    @Test
    public void testPersist_withoutDate_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDate(null);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }


    @Test
    public void testPersist_withoutDuration_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDuration(null);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withNegativeDuration_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDuration(-1);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withZeroDuration_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        training.setDuration(0);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withoutTrainingType_throwsException() {
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(null, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withoutTrainee_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, null, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withoutTrainer_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var training = nextTraining(type, trainee, null);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withNewTrainingType_throwsException() {
        var type = nextTrainingType();
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withNewTrainee_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = nextTrainee(null);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testPersist_withNewTrainer_throwsException() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = nextTrainer(type, null);
        var training = nextTraining(type, trainee, trainer);
        assertThrows(ConstraintViolationException.class,
                () -> manager.persist(training));
    }

    @Test
    public void testRemove() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var training = nextTraining(type, trainee, trainer);
        manager.persist(training);
        var id = training.getId();

        manager.remove(training);
        assertNotNull(manager.find(Trainer.class, 4L));
        assertNotNull(manager.find(Trainee.class, 1L));
        assertNotNull(manager.find(TrainingType.class, 1L));
        assertNull(manager.find(Training.class, id));
    }

}
