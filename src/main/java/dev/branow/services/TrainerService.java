package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.*;
import dev.branow.mappers.TrainerMapper;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingTypeRepository;
import jakarta.transaction.Transactional;
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
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    @Authenticate
    @Log("getting all not assigned trainers on trainee by username %0")
    public List<TrainerDto> getAllNotAssignedOnTraineeByTraineeUsername(String username) {
        return repository.findAllNotAssignedOnTraineeByTraineeUsername(username).stream()
                .map(mapper::toTrainerDto).toList();
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Id.class)
    @Log("getting trainer by id %0")
    public TrainerDto getById(Long id) {
        return mapper.toTrainerDto(repository.getReferenceById(id));
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("getting trainer by username %0")
    public TrainerDto getByUsername(String username) {
        return mapper.toTrainerDto(repository.getReferenceByUsername(username));
    }

    @Transactional
    @Log("creating trainer with %0")
    public TrainerDto create(@Valid CreateTrainerDto dto) {
        var trainer = mapper.toTrainer(dto);
        userService.prepareUserForCreation(trainer);
        var trainingType = trainingTypeRepository.getReferenceById(dto.getSpecialization());
        trainer.setSpecialization(trainingType);
        var savedTrainer = repository.save(trainer);
        return mapper.toTrainerDto(savedTrainer);
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.UpdateTrainerDto.class)
    @Log("updating trainer with %0")
    public TrainerDto update(@Valid UpdateTrainerDto dto) {
        var trainer = repository.getReferenceById(dto.getId());
        var trainingType = trainingTypeRepository.getReferenceById(dto.getSpecialization());
        trainer.setSpecialization(trainingType);
        userService.applyUserUpdates(trainer, dto);
        return mapper.toTrainerDto(trainer);
    }

}
