package dev.branow.auth.authorizers;

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
        return ( username, credentials) -> {
            authorizer.authorize(credentials, () -> repository.getReferenceByUsername(username));
        };
    }

    @Bean
    public UserAuthorizer.Id authorizeUserById() {
        return ( id, credentials) -> {
            authorizer.authorize(credentials, () -> repository.getReferenceById(id));
        };
    }

    @Bean
    public UserAuthorizer.UpdateTraineeDto authorizeUserByUpdateTraineeDto() {
        return (updateTraineeDto,  credentials) -> {
            authorizer.authorize(credentials, () -> repository.getReferenceById(updateTraineeDto.getId()));
        };
    }

    @Bean
    public UserAuthorizer.UpdateTrainerDto authorizeUserByUpdateTrainerDto() {
        return (updateTrainerDto,  credentials) -> {
            authorizer.authorize(credentials, () -> repository.getReferenceById(updateTrainerDto.getId()));
        };
    }

    @Bean
    public TrainingAuthorizer.CriteriaTraineeDto authorizeUserByCriteriaTraineeDto() {
        return (criteriaTraineeDto,  credentials) -> {
            authorizer.authorize(credentials, () -> repository.getReferenceByUsername(criteriaTraineeDto.getTraineeUsername()));
        };
    }

    @Bean
    public TrainingAuthorizer.CriteriaTrainerDto authorizeUserByCriteriaTrainerDto() {
        return (criteriaTrainerDto,  credentials) -> {
            authorizer.authorize(credentials, () -> repository.getReferenceByUsername(criteriaTrainerDto.getTrainerUsername()));
        };
    }

}
