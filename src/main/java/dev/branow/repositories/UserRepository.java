package dev.branow.repositories;

import dev.branow.model.User;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    default User getReferenceByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(User.class.getSimpleName(), (Object) username));
    }

    User getByUsername(String username);

}
