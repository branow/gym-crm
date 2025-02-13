package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.TrainingAuthorizer;
import dev.branow.dtos.CreateTrainingDto;
import dev.branow.dtos.CriteriaTrainingTraineeDto;
import dev.branow.dtos.CriteriaTrainingTrainerDto;
import dev.branow.dtos.TrainingDto;
import dev.branow.mappers.TrainingMapper;
import dev.branow.model.Training;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingRepository;
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
public class TrainingService {

    private final TrainingRepository repository;
    private final TrainingMapper mapper;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    @Authenticate
    @Log("creating training with %0")
    public TrainingDto create(@Valid CreateTrainingDto dto) {
        var type = trainingTypeRepository.getReferenceById(dto.getTypeId());
        var trainee = traineeRepository.getReferenceById(dto.getTraineeId());
        var trainer = trainerRepository.getReferenceById(dto.getTrainerId());
        var training = mapper.toTraining(dto);

        training.setType(type);
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        var savedTraining = repository.save(training);
        return mapper.toTrainingDto(savedTraining);
    }

    @Authenticate
    @Authorize(TrainingAuthorizer.CriteriaTraineeDto.class)
    @Log("getting all trainings for trainee %0")
    public List<TrainingDto> getAllByTraineeUsernameCriteria(@Valid CriteriaTrainingTraineeDto dto) {
        return repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                dto.getTypeId()
        ).stream().map(mapper::toTrainingDto).toList();
    }

    @Authenticate
    @Authorize(TrainingAuthorizer.CriteriaTrainerDto.class)
    @Log("getting all trainings for trainer %0")
    public List<TrainingDto> getAllByTrainerUsernameCriteria(@Valid CriteriaTrainingTrainerDto dto) {
        return repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                null
        ).stream().map(mapper::toTrainingDto).toList();
    }

}
