package dev.branow.repositories;

import dev.branow.DBTest;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeRepositoryTest extends DBTest {

    @Autowired
    private EntityManager manager;
    @Autowired
    private TraineeRepository repository;

    @Test
    @Transactional
    public void testDelete() {
        var trainee = manager.find(Trainee.class, 1L);
        var trainingsIds = trainee.getTrainings().stream().map(Training::getId).toList();
        repository.deleteById(trainee.getId());
        trainingsIds.forEach(trainingId -> assertNull(manager.find(Training.class, trainingId)));
    }

    @Test
    @Transactional
    public void testSave_withNewFavoriteTrainers_saveNewRelations() {
        var trainee = manager.find(Trainee.class, 1L);
        var trainer1 = manager.find(Trainer.class, 9L);
        var trainer2 = manager.find(Trainer.class, 10L);
        trainee.getFavoriteTrainers().add(trainer1);
        trainee.getFavoriteTrainers().add(trainer2);
        var trainerIds = trainee.getFavoriteTrainers().stream().map(Trainer::getId).toList();
        manager.flush();
        var query = String.format("select trainer_id from trainee_favorite_trainers where trainee_id = %d", trainee.getId());
        var actualTrainerIds = manager.createNativeQuery(query, Long.class).getResultList().stream().toList();
        assertEquals(trainerIds, actualTrainerIds);
    }

    @Test
    @Transactional
    public void testSave_withRepeatedFavoriteTrainers_throwsException() {
        var trainee = manager.find(Trainee.class, 1L);
        var trainer1 = manager.find(Trainer.class, 4L);
        trainee.getFavoriteTrainers().add(trainer1);
        assertThrows(ConstraintViolationException.class, () -> manager.flush());
    }

    @Test
    @Transactional
    public void testSave_withAbsentFavoriteTrainers_removeRelations() {
        var trainee = manager.find(Trainee.class, 1L);
        trainee.getFavoriteTrainers().remove(1);
        var trainerIds = trainee.getFavoriteTrainers().stream().map(Trainer::getId).toList();
        manager.flush();
        var query = String.format("select trainer_id from trainee_favorite_trainers where trainee_id = %d", trainee.getId());
        var actualTrainerIds = manager.createNativeQuery(query, Long.class).getResultList().stream().toList();
        assertEquals(trainerIds, actualTrainerIds);
    }

}
