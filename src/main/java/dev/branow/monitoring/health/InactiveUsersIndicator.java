package dev.branow.monitoring.health;

import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("inactiveUsers")
@RequiredArgsConstructor
public class InactiveUsersIndicator implements HealthIndicator {

    private final UserRepository repository;

    @Override
    public Health health() {
        var users = getInactiveUsers();
        return Optional.of(users)
                .filter(List::isEmpty)
                .map(_ -> Health.up()
                        .withDetail("status", "There are no inactive users"))
                .orElse(Health.down()
                        .withDetail("status", "There are inactive users")
                        .withDetail("count", users.size())
                        .withDetail("usernames", users.stream().map(User::getUsername).toArray(String[]::new)))
                .build();
    }

    private List<User> getInactiveUsers() {
        return repository.findAll().stream()
                .filter(user -> !user.getIsActive())
                .toList();
    }

}
