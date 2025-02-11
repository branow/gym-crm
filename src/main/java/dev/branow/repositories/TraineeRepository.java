package dev.branow.repositories;

import dev.branow.model.Trainee;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface TraineeRepository extends ListCrudRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
    void deleteByUsername(String username);
}
