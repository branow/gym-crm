package dev.branow.model;

import dev.branow.TestDBConfig;
import dev.branow.TestDataFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static dev.branow.EntityManagerUtils.*;
import static dev.branow.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig(TestDBConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestTrainer {

    @Autowired
    private EntityManager manager;

    @AfterEach
    public void cleanUp() {
        clean(manager);
    }

    @Test
    public void testFind() {
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .name("Strength Training")
                .build();
        List<Training> trainings = List.of(
                Training.builder()
                        .id(1L)
                        .name("Full Body Strength")
                        .date(LocalDate.of(2024, 2, 10))
                        .duration(60)
                        .type(trainingType)
                        .build(),
                Training.builder()
                        .id(8L)
                        .name("Upper Body Strength")
                        .date(LocalDate.of(2024, 2, 17))
                        .duration(45)
                        .type(trainingType)
                        .build()
        );
        Trainer trainer = new Trainer();
        trainer.setId(4L);
        trainer.setFirstName("Emma");
        trainer.setLastName("Wilson");
        trainer.setUsername("Emma.Wilson");
        trainer.setPassword("hZ29UTGBHk");
        trainer.setIsActive(true);
        trainer.setSpecialization(trainingType);
        trainer.setTrainings(trainings);

        var actual = manager.find(Trainer.class, trainer.getId());
        assertNotNull(actual);
        assertEquals(trainer, actual);
    }

    @Test
    @Transactional
    public void testPersist_withoutSpecialization_throwsException() {
        var trainer = nextTrainer(null, null);
        assertThrows(ConstraintViolationException.class, () -> persist(manager, trainer));
    }

    @Test
    @Transactional
    public void testPersist_withoutTrainings_persistTrainer() {
        var spec = manager.find(TrainingType.class, 1L);
        var trainer = nextTrainer(spec, null);

        var expectedTrainerCount = count(manager, Trainer.class) + 1;
        var expectedUserCount = count(manager, User.class) + 1;
        var expectedId = lastId(manager, User.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(trainer);
        expected.setId(expectedId);

        persist(manager, trainer);
        assertEquals(expected, trainer);

        var actualUserCount = count(manager, User.class);
        var actualTrainerCount = count(manager, Trainer.class);
        assertEquals(expectedUserCount, actualUserCount);
        assertEquals(expectedTrainerCount, actualTrainerCount);
    }

    @Test
    @Transactional
    public void testPersist_withNewTrainings_persistTraineeAndTrainings() {
        var spec = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = nextTrainer(spec, null);
        var type = manager.find(TrainingType.class, 1L);
        var training = nextTraining(type, trainee, trainer);
        trainer.setTrainings(List.of(training));

        var expectedTrainerCount = count(manager, Trainer.class) + 1;
        var expectedTrainingCount = count(manager, Training.class) + 1;
        persist(manager, trainer);
        assertFalse(trainer.getTrainings().isEmpty());
        assertNotNull(trainer.getTrainings().get(0).getId());

        var actualTrainerCount = count(manager, Trainer.class);;
        var actualTrainingCount = count(manager, Training.class);;
        assertEquals(expectedTrainerCount, actualTrainerCount);
        assertEquals(expectedTrainingCount, actualTrainingCount);
    }

    @Test
    @Transactional
    public void testMerge_withUpdatedTrainings_updateTrainings() {
        var trainee1 = manager.find(Trainee.class, 1L);
        var trainee2 = manager.find(Trainee.class, 2L);
        var spec = manager.find(TrainingType.class, 1L);
        var trainer = nextTrainer(spec, null);
        var training1 = nextTraining(trainer.getSpecialization(), trainee1, trainer);
        var training2 = nextTraining(trainer.getSpecialization(), trainee1, trainer);
        var training3 = nextTraining(trainer.getSpecialization(), trainee2, trainer);
        trainer.setTrainings(new ArrayList<>(List.of(training1, training2)));
        persist(manager, trainer);

        trainer = TestDataFactory.clone(trainer);
        trainer.getTrainings().remove(training1);
        training2.setTrainee(trainee2);
        trainer.getTrainings().add(training3);
        persist(manager, training3);
        merge(manager, trainer);

        assertNotNull(manager.find(Trainer.class, trainer.getId()));
        assertNotNull(manager.find(Trainee.class, trainee1.getId()));
        assertNotNull(manager.find(Trainee.class, trainee2.getId()));
        assertNull(manager.find(Training.class, training1.getId()));
        assertNotNull(manager.find(Training.class, training2.getId()));
        assertNotNull(manager.find(Training.class, training3.getId()));
    }

    @Test
    @Transactional
    public void testRemove_withoutTrainings_removeTraineeAndUser() {
        var spec = manager.find(TrainingType.class, 1L);
        var trainer = nextTrainer(spec, null);

        persist(manager, trainer);
        var id = trainer.getId();

        remove(manager, trainer);
        assertNull(manager.find(User.class, id));
        assertNull(manager.find(Trainer.class, id));
        assertNotNull(manager.find(TrainingType.class, spec.getId()));
    }

    @Test
    @Transactional
    public void testRemove_withTrainings_removeTraineeAndUser() {
        var type = manager.find(TrainingType.class, 1L);
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = nextTrainer(type, null);
        var trainings = List.of(
                nextTraining(type, trainee, trainer),
                nextTraining(type, trainee, trainer)
        );
        trainer.setTrainings(trainings);

        persist(manager, trainer);
        var id = trainer.getId();

        remove(manager, trainer);
        assertNull(manager.find(Trainer.class, id));
        trainings.stream()
                .map(Training::getId)
                .forEach((trainingId) -> assertNull(manager.find(Training.class, trainingId)));
    }

}
