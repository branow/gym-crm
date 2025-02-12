package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.TrainingAuthorizer;
import dev.branow.dtos.CreateTrainingDto;
import dev.branow.dtos.CriteriaTrainingTraineeDto;
import dev.branow.dtos.CriteriaTrainingTrainerDto;
import dev.branow.mappers.TrainingMapper;
import dev.branow.model.Training;
import dev.branow.repositories.TrainingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository repository;
    private final TrainingMapper mapper;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;

    @Authenticate
    @Log("creating training with %0")
    public Training create(@Valid CreateTrainingDto dto) {
        var type = trainingTypeService.getById(dto.getTypeId());
        var trainee = traineeService.getById(dto.getTraineeId());
        var trainer = trainerService.getById(dto.getTrainerId());
        var training = mapper.toTraining(dto);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(type);
        return repository.save(training);
    }

    @Authenticate
    @Authorize(TrainingAuthorizer.CriteriaTraineeDto.class)
    @Log("getting all trainings for trainee %0")
    public List<Training> getAllByTraineeUsernameCriteria(@Valid CriteriaTrainingTraineeDto dto) {
        return repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                dto.getTypeId()
        );
    }

    @Authenticate
    @Authorize(TrainingAuthorizer.CriteriaTrainerDto.class)
    @Log("getting all trainings for trainer %0")
    public List<Training> getAllByTrainerUsernameCriteria(@Valid CriteriaTrainingTrainerDto dto) {
        return repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                null
        );
    }

}
