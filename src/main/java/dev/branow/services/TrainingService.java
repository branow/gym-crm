package dev.branow.services;

import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository repository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public Training getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Training.class, id));
    }

    public List<Training> getAll() {
        return repository.findAll().collect(Collectors.toList());
    }

    public Training create(Training training) {
        traineeRepository.findById(training.getTraineeId())
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class, training.getTraineeId()));
        trainerRepository.findById(training.getTrainerId())
                .orElseThrow(() -> new EntityNotFoundException(Trainer.class, training.getTrainerId()));
        return repository.create(training);
    }

}
