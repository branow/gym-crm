package dev.branow.security.authorization;

import dev.branow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Authorizers {

    private final UserRepository repository;
    private final SimpleUserAuthorizer authorizer;

    @Bean
    public UserAuthorizer.Username authorizeUserByUsername() {
        return (username, authentication) -> {
            authorizer.authorize(() -> repository.getReferenceByUsername(username), authentication);
        };
    }

    @Bean
    public UserAuthorizer.CreateTrainingRequest authorizeCreateTrainingRequest() {
        return (training, authentication) -> {
            authorizer.authorize(() -> {
                if (training.getTrainee().equals(authentication.getName())) {
                    return repository.getReferenceByUsername(training.getTrainee());
                } else if (training.getTrainer().equals(authentication.getName())) {
                    return repository.getReferenceByUsername(training.getTrainer());
                }
                throw new AccessDeniedException("You are not allowed to create training");
            }, authentication);
        };
    }

}
