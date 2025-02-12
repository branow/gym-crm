package dev.branow.auth.authorizers;

import dev.branow.auth.Credentials;
import dev.branow.exceptions.AccessDeniedException;
import dev.branow.model.User;
import dev.branow.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Authorizers {

    private final UserService service;

    @Bean
    public UserAuthorizer.Username authorizeUserByUsername() {
        return ( username, credentials) -> {
            var user = service.getByUsername(username);
            authorizeUser(user, credentials);
        };
    }

    @Bean
    public UserAuthorizer.Id authorizeUserById() {
        return ( id, credentials) -> {
            var user = service.getById(id);
            authorizeUser(user, credentials);
        };
    }

    @Bean
    public UserAuthorizer.UpdateTraineeDto authorizeUserByUpdateTraineeDto() {
        return (updateTraineeDto,  credentials) -> {
            var user = service.getById(updateTraineeDto.getId());
            authorizeUser(user, credentials);
        };
    }

    @Bean
    public UserAuthorizer.UpdateTrainerDto authorizeUserByUpdateTrainerDto() {
        return (updateTrainerDto,  credentials) -> {
            var user = service.getById(updateTrainerDto.getId());
            authorizeUser(user, credentials);
        };
    }

    @Bean
    public TrainingAuthorizer.CriteriaTraineeDto authorizeUserByCriteriaTraineeDto() {
        return (criteriaTraineeDto,  credentials) -> {
            var user = service.getByUsername(criteriaTraineeDto.getTraineeUsername());
            authorizeUser(user, credentials);
        };
    }

    @Bean
    public TrainingAuthorizer.CriteriaTrainerDto authorizeUserByCriteriaTrainerDto() {
        return (criteriaTrainerDto,  credentials) -> {
            var user = service.getByUsername(criteriaTrainerDto.getTraineeUsername());
            authorizeUser(user, credentials);
        };
    }

    private void authorizeUser(User user, Credentials credentials) {
        if (!user.getUsername().equals(credentials.getUsername())) {
            throw new AccessDeniedException("You are not allowed to access this user");
        }
    }

}
