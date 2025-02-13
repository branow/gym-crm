package dev.branow.repositories;

import dev.branow.DBTest;
import dev.branow.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerRepositoryTest extends DBTest {

    @Autowired
    private EntityManager manager;
    @Autowired
    private TrainerRepository repository;

    @Test
    @Transactional
    public void testFindAll() {
        var trainers = manager.createQuery("SELECT t FROM trainers t", Trainer.class).getResultList();
        var actual = repository.findAll();
        assertEquals(trainers, actual);
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(strings = {"John.Doe", "Bob.Brown", "James.Taylor", "Sophia.White"})
    public void testFindAllNotAssignedOnTraineeByTraineeUsername(String username) {
        Comparator<Trainer> comparator = Comparator.comparing(Trainer::getUsername);
        var trainers = manager.createQuery("SELECT t FROM trainers t", Trainer.class).getResultList();
        var expected = trainers.stream().filter(trainer ->
                trainer.getTrainings().isEmpty() ||
                trainer.getTrainings().stream().noneMatch(training -> training.getTrainee().getUsername().equals(username))
                ).sorted(comparator).toList();
        var actual = repository.findAllNotAssignedOnTraineeByTraineeUsername(username)
                .stream().sorted(comparator).toList();
        assertEquals(expected, actual);
    }

}
