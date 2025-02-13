package dev.branow.model;

import dev.branow.DBTest;
import dev.branow.TestDataFactory;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static dev.branow.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestTrainee extends DBTest {

    @Test
    public void testPersist_withTooLongAddress_throwsException() {
        var validTrainee = nextTrainee(null);
        validTrainee.setAddress("a".repeat(255));
        manager.persist(validTrainee);

        var invalidTrainee = nextTrainee(null);
        invalidTrainee.setAddress("a".repeat(256));
        assertThrows(DataException.class,
                () -> manager.persist(invalidTrainee));
    }

    @Test
    public void testPersist_withoutTrainings_persistTrainee() {
        var trainee = nextTrainee(null);

        var expectedTraineeCount = manager.count(Trainee.class) + 1;
        var expectedUserCount = manager.count(User.class) + 1;
        var expectedId = manager.lastId(User.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(trainee);
        expected.setId(expectedId);

        manager.persist(trainee);
        assertEquals(expected, trainee);

        var actualUserCount = manager.count(User.class);
        var actualTraineeCount = manager.count(Trainee.class);
        assertEquals(expectedUserCount, actualUserCount);
        assertEquals(expectedTraineeCount, actualTraineeCount);
    }

    @Test
    public void testPersist_withNewTrainings_persistTraineeAndTrainings() {
        var trainer = manager.find(Trainer.class, 4L);
        var trainee = nextTrainee(null);
        var type = manager.find(TrainingType.class, 1L);
        var training = nextTraining(type, trainee, trainer);
        trainee.setTrainings(List.of(training));

        var expectedTraineeCount = manager.count(Trainee.class) + 1;
        var expectedTrainingCount = manager.count(Training.class) + 1;
        manager.persist(trainee);
        assertFalse(trainee.getTrainings().isEmpty());
        assertNotNull(trainee.getTrainings().get(0).getId());

        var actualTraineeCount = manager.count(Trainee.class);;
        var actualTrainingCount = manager.count(Training.class);;
        assertEquals(expectedTraineeCount, actualTraineeCount);
        assertEquals(expectedTrainingCount, actualTrainingCount);
    }

    @Test
    public void testMerge_withUpdatedTrainings_doNotUpdateTrainings() {
        var trainer1 = manager.find(Trainer.class, 4L);
        var trainer2 = manager.find(Trainer.class, 5L);
        var trainee = nextTrainee(null);
        var training1 = nextTraining(trainer1.getSpecialization(), trainee, trainer1);
        var training2 = nextTraining(trainer1.getSpecialization(), trainee, trainer1);
        var training3 = nextTraining(trainer2.getSpecialization(), trainee, trainer2);
        trainee.setTrainings(new ArrayList<>(List.of(training1, training2)));
        manager.persist(trainee);

        trainee = TestDataFactory.clone(trainee);
        trainee.getTrainings().remove(training1);
        training2.setTrainer(trainer2);
        training2.setType(trainer2.getSpecialization());
        trainee.getTrainings().add(training3);
        manager.persist(training3);
        manager.merge(trainee);

        assertNotNull(manager.find(Trainee.class, trainee.getId()));
        assertNotNull(manager.find(Trainer.class, trainer1.getId()));
        assertNotNull(manager.find(Trainer.class, trainer2.getId()));
        assertNotNull(manager.find(Training.class, training1.getId()));
        assertNotNull(manager.find(Training.class, training2.getId()));
        assertNotNull(manager.find(Training.class, training3.getId()));
    }

    @Test
    public void testRemove_withoutTrainings_removeTraineeAndUser() {
        var trainee = nextTrainee(null);

        manager.persist(trainee);
        var id = trainee.getId();

        manager.remove(trainee);
        assertNull(manager.find(User.class, id));
        assertNull(manager.find(Trainer.class, id));
    }

    @Test
    public void testRemove_withTrainings_removeTraineeAndUser() {
        var type = manager.find(TrainingType.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);
        var trainee = nextTrainee(null);
        var trainings = List.of(
                nextTraining(type, trainee, trainer),
                nextTraining(type, trainee, trainer)
        );
        trainee.setTrainings(trainings);

        manager.persist(trainee);
        var id = trainee.getId();

        manager.remove(trainee);
        assertNull(manager.find(Trainer.class, id));
        trainings.stream()
                .map(Training::getId)
                .forEach((trainingId) -> assertNull(manager.find(Training.class, trainingId)));
    }

}
