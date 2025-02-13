package dev.branow.repositories;

import dev.branow.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByCriteria(
            String traineeUsername,
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            Long typeId
    );
}
