package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.*;
import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.mappers.TrainerMapper;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.repositories.TrainerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository repository;
    private final TrainerMapper mapper;
    private final UserService userService;
    private final TrainingTypeService trainingTypeService;

    @Authenticate
    @Log("getting all not assigned trainers on trainee by username %0")
    public List<Trainer> getAllNotAssignedOnTraineeByTraineeUsername(String username) {
        return repository.findAllNotAssignedOnTraineeByTraineeUsername(username);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Id.class)
    @Log("getting trainer by id %0")
    public Trainer getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class, id));
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("getting trainer by username %0")
    public Trainer getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class, username));
    }

    @Log("creating trainer with %0")
    public Trainer create(@Valid CreateTrainerDto dto) {
        var trainer = mapper.toTrainer(dto);
        var trainingType = trainingTypeService.getById(dto.getSpecialization());
        trainer.setSpecialization(trainingType);
        userService.prepareUserForCreation(trainer);
        return repository.save(trainer);
    }

    @Authenticate
    @Authorize(UserAuthorizer.UpdateTrainerDto.class)
    @Log("updating trainer with %0")
    public Trainer update(@Valid UpdateTrainerDto dto) {
        var trainer = getById(dto.getId());
        var trainingType = trainingTypeService.getById(dto.getSpecialization());
        trainer.setSpecialization(trainingType);
        userService.applyUserUpdates(trainer, dto);
        return repository.save(trainer);
    }

}
