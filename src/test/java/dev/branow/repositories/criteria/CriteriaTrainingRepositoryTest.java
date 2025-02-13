package dev.branow.repositories.criteria;

import dev.branow.DBTest;
import dev.branow.model.Training;
import dev.branow.repositories.TrainingRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(CriteriaTrainingRepository.class)
public class CriteriaTrainingRepositoryTest extends DBTest {

    @Autowired
    private TrainingRepository repository;

    @ParameterizedTest
    @MethodSource("provideTestFindAllByCriteria")
    public void testFindAllByCriteria(
            Predicate<Training> criteria,
            String traineeUsername,
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            Long typeId
    ) {
        var trainings = manager.findAll(Training.class);

        var expected = trainings.stream().filter(criteria).toList();
        var actual = repository.findAllByCriteria(traineeUsername, trainerUsername, from, to, typeId);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> provideTestFindAllByCriteria() {
        var traineeUsername = "John.Doe";
        var trainerUsername = "Emma.Wilson";
        var dateFrom = LocalDate.of(2024, 2, 14);
        var dateTo = LocalDate.of(2024, 2, 14);
        var typeId = 2L;
        return Stream.of(
                Arguments.of(
                        (Predicate<Training>) _ -> true,
                        null, null, null, null, null
                ),
                Arguments.of(
                        (Predicate<Training>) training -> training.getTrainee().getUsername().equals(traineeUsername),
                        traineeUsername, null, null, null, null
                ),
                Arguments.of(
                        (Predicate<Training>) training -> training.getTrainer().getUsername().equals(trainerUsername),
                        null, trainerUsername, null, null, null
                ),
                Arguments.of(
                        (Predicate<Training>) training -> training.getDate().isEqual(dateFrom) || training.getDate().isAfter(dateFrom),
                        null, null, dateFrom, null, null
                ),
                Arguments.of(
                        (Predicate<Training>) training -> training.getDate().isEqual(dateTo) || training.getDate().isBefore(dateTo),
                        null, null, null, dateTo, null
                ),
                Arguments.of(
                        (Predicate<Training>) training -> training.getType().getId().equals(typeId),
                        null, null, null, null, typeId
                )
        );
    }

}
