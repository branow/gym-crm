package dev.branow.monitoring.health;

import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("activeUsers")
@RequiredArgsConstructor
public class ActiveUsersHealthIndicator implements HealthIndicator {

    private final UserRepository repository;

    @Override
    public Health health() {
        var count = getActiveUsers();
        return Optional.of(count)
                .filter(_ -> count > 0)
                .map(_ -> Health.up()
                        .withDetail("status", "There is at least one active users")
                        .withDetail("count", count)
                        .build())
                .orElse(Health.down()
                        .withDetail("status", "There are not active users")
                        .build());
    }

    private int getActiveUsers() {
        return (int) repository.findAll().stream()
                .filter(User::getIsActive)
                .count();
    }

}
