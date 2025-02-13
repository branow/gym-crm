package dev.branow.repositories;

import dev.branow.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Trainee getReferenceByUsername(String username);
    Trainee getByUsername(String username);
    void deleteByUsername(String username);
}
