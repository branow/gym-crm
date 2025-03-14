package dev.branow.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 3;
    public static final TemporalAmount LOCK_TIME = Duration.ofMinutes(5);

    private final Map<String, FailedLoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public void recordFailure(String key) {
        var attempt = Optional.ofNullable(loginAttempts.get(key))
                .map(FailedLoginAttempt::increment)
                .orElse(new FailedLoginAttempt());
        loginAttempts.put(key, attempt);
    }

    public void recordSuccess(String key) {
        loginAttempts.remove(key);
    }

    public boolean isBlocked(String key) {
        return Optional.ofNullable(loginAttempts.get(key))
                .map(FailedLoginAttempt::isBlocked)
                .orElse(false);
    }

    private static class FailedLoginAttempt {
        private int count;
        private LocalDateTime last;

        public FailedLoginAttempt() {
            this.count = 1;
            this.last = LocalDateTime.now();
        }

        public FailedLoginAttempt increment() {
            count++;
            last = LocalDateTime.now();
            return this;
        }

        public boolean isBlocked() {
            return count >= MAX_ATTEMPTS && LocalDateTime.now().isBefore(last.plus(LOCK_TIME));
        }
    }

}
