package dev.branow.auth.authorizers;

import dev.branow.exceptions.AccessDeniedException;
import dev.branow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Authorizers {

    private final UserRepository repository;
    private final SimpleUserAuthorizer authorizer;

    @Bean
    public UserAuthorizer.Username authorizeUserByUsername() {
        return (username, credentials) -> {
            authorizer.authorize(credentials, () -> repository.getReferenceByUsername(username));
        };
    }

    @Bean
    public UserAuthorizer.CreateTrainingRequest authorizeCreateTrainingRequest() {
        return (training, credentials) -> {
            authorizer.authorize(credentials, () -> {
                if (training.getTrainee().equals(credentials.getUsername())) {
                    return repository.getReferenceByUsername(training.getTrainee());
                } else if (training.getTrainer().equals(credentials.getUsername())) {
                    return repository.getReferenceByUsername(training.getTrainer());
                }
                throw new AccessDeniedException("You are not allowed to create training");
            });
        };
    }

}
