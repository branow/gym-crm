package dev.branow.repositories;

import dev.branow.model.Trainer;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUsername(String username);

    default Trainer getReferenceByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(Trainer.class.getSimpleName(), (Object) username));
    }

    @Query("""
        SELECT DISTINCT ter FROM trainers ter
        WHERE NOT EXISTS (
            SELECT tng FROM ter.trainings tng
            WHERE tng.trainee.username = :username
        )
    """)
    List<Trainer> findAllNotAssignedOnTraineeByTraineeUsername(@Param("username") String username);

}
