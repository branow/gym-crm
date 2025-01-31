package dev.branow.services;

import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository repository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public Training getById(Long id) {
        log.debug("Getting training with id {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Training with id {} not found", id);
                    return new EntityNotFoundException(Training.class, id);
                });
    }

    public List<Training> getAll() {
        return repository.findAll().collect(Collectors.toList());
    }

    public Training create(Training training) {
        log.info("Creating new training {}", training);

        traineeRepository.findById(training.getTraineeId())
                .orElseThrow(() -> {
                    log.warn("Trainee with id {} not found", training.getTraineeId());
                    return new EntityNotFoundException(Trainee.class, training.getTraineeId());
                });

        trainerRepository.findById(training.getTrainerId())
                .orElseThrow(() -> {
                    log.warn("Trainer with id {} not found", training.getTrainerId());
                    return new EntityNotFoundException(Trainer.class, training.getTrainerId());
                });

        var newTraining = repository.create(training);
        log.info("Training created successfully {}", newTraining);
        return newTraining;
    }

}
