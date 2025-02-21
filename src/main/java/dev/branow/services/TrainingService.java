package dev.branow.services;

import dev.branow.annotations.Log;
import dev.branow.dtos.service.CreateTrainingDto;
import dev.branow.dtos.service.CriteriaTrainingTraineeDto;
import dev.branow.dtos.service.CriteriaTrainingTrainerDto;
import dev.branow.dtos.service.TrainingDto;
import dev.branow.mappers.TrainingMapper;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository repository;
    private final TrainingMapper mapper;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Transactional
    @Log("creating training with %0")
    public TrainingDto create(CreateTrainingDto dto) {
        var trainee = traineeRepository.getReferenceByUsername(dto.getTrainee());
        var trainer = trainerRepository.getReferenceByUsername(dto.getTrainer());
        var training = mapper.toTraining(dto);

        training.setType(trainer.getSpecialization());
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        var savedTraining = repository.save(training);
        return mapper.toTrainingDto(savedTraining);
    }

    @Log("getting all trainings for trainee %0")
    public List<TrainingDto> getAllByTraineeUsernameCriteria(CriteriaTrainingTraineeDto dto) {
        return repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                dto.getTypeId()
        ).stream().map(mapper::toTrainingDto).toList();
    }

    @Log("getting all trainings for trainer %0")
    public List<TrainingDto> getAllByTrainerUsernameCriteria(CriteriaTrainingTrainerDto dto) {
        return repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                null
        ).stream().map(mapper::toTrainingDto).toList();
    }

}
