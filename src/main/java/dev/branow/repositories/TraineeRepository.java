package dev.branow.repositories;

import dev.branow.model.Trainee;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
    Trainee getByUsername(String username);
    void deleteByUsername(String username);
    default Trainee getReferenceByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(Trainee.class.getSimpleName(), (Object) username));
    }
}
