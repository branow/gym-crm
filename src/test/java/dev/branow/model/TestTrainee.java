package dev.branow.model;

import dev.branow.MockDB;
import dev.branow.TestDBConfig;
import dev.branow.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static dev.branow.EntityManagerUtils.*;
import static dev.branow.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestDBConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTrainee {

    @Autowired
    private EntityManager manager;

    @AfterEach
    public void cleanUp() {
        clean(manager);
    }

    @Test
    public void testFind() {
        TrainingType trainingType1 = TrainingType.builder()
                .id(1L)
                .name("Strength Training")
                .build();
        TrainingType trainingType2 = TrainingType.builder()
                .id(2L)
                .name("Cardio Workouts")
                .build();
        List<Training> trainings = List.of(
                Training.builder()
                        .id(1L)
                        .name("Full Body Strength")
                        .date(LocalDate.of(2024, 2, 10))
                        .duration(60)
                        .type(trainingType1)
                        .build(),
                Training.builder()
                        .id(4L)
                        .name("Interval Running")
                        .date(LocalDate.of(2024, 2, 13))
                        .duration(30)
                        .type(trainingType2)
                        .build()
        );
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setPassword("RM9AVLZpCK");
        trainee.setIsActive(true);
        trainee.setDateOfBirth(LocalDate.of(1990, 5, 15));
        trainee.setAddress("123 Main St, City A");
        trainee.setTrainings(trainings);

        var actual = manager.find(Trainee.class, 1L);
        assertNotNull(actual);
        assertEquals(trainee, actual);
    }

    @Test
    public void testPersist_withTooLongAddress_throwsException() {
        var validTrainee = nextTrainee(null);
        validTrainee.setAddress("a".repeat(255));
        persist(manager, validTrainee);

        var invalidTrainee = nextTrainee(null);
        invalidTrainee.setAddress("a".repeat(256));
        assertThrows(DataException.class,
                () -> persist(manager, invalidTrainee));
    }

    @Test
    public void testPersist_withoutTrainings_persistTrainee() {
        var trainee = nextTrainee(null);

        var expectedTraineeCount = count(manager, Trainee.class) + 1;
        var expectedUserCount = count(manager, User.class) + 1;
        var expectedId = lastId(manager, User.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(trainee);
        expected.setId(expectedId);

        persist(manager, trainee);
        assertEquals(expected, trainee);

        var actualUserCount = count(manager, User.class);
        var actualTraineeCount = count(manager, Trainee.class);
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

        var expectedTraineeCount = count(manager, Trainee.class) + 1;
        var expectedTrainingCount = count(manager, Training.class) + 1;
        persist(manager, trainee);
        assertFalse(trainee.getTrainings().isEmpty());
        assertNotNull(trainee.getTrainings().get(0).getId());

        var actualTraineeCount = count(manager, Trainee.class);;
        var actualTrainingCount = count(manager, Training.class);;
        assertEquals(expectedTraineeCount, actualTraineeCount);
        assertEquals(expectedTrainingCount, actualTrainingCount);
    }

    @Test
    public void testMerge_withUpdatedTrainings_updateTrainings() {
        var trainer1 = manager.find(Trainer.class, 4L);
        var trainer2 = manager.find(Trainer.class, 5L);
        var trainee = nextTrainee(null);
        var training1 = nextTraining(trainer1.getSpecialization(), trainee, trainer1);
        var training2 = nextTraining(trainer1.getSpecialization(), trainee, trainer1);
        var training3 = nextTraining(trainer2.getSpecialization(), trainee, trainer2);
        trainee.setTrainings(new ArrayList<>(List.of(training1, training2)));
        persist(manager, trainee);

        trainee = TestDataFactory.clone(trainee);
        trainee.getTrainings().remove(training1);
        training2.setTrainer(trainer2);
        training2.setType(trainer2.getSpecialization());
        trainee.getTrainings().add(training3);
        persist(manager, training3);
        merge(manager, trainee);

        assertNotNull(manager.find(Trainee.class, trainee.getId()));
        assertNotNull(manager.find(Trainer.class, trainer1.getId()));
        assertNotNull(manager.find(Trainer.class, trainer2.getId()));
        assertNull(manager.find(Training.class, training1.getId()));
        assertNotNull(manager.find(Training.class, training2.getId()));
        assertNotNull(manager.find(Training.class, training3.getId()));
    }

    @Test
    public void testRemove_withoutTrainings_removeTraineeAndUser() {
        var trainee = nextTrainee(null);

        persist(manager, trainee);
        var id = trainee.getId();

        remove(manager, trainee);
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

        persist(manager, trainee);
        var id = trainee.getId();

        remove(manager, trainee);
        assertNull(manager.find(Trainer.class, id));
        trainings.stream()
                .map(Training::getId)
                .forEach((trainingId) -> assertNull(manager.find(Training.class, trainingId)));
    }

}
