package dev.branow.mappers;

import dev.branow.dtos.service.CreateTrainerDto;
import dev.branow.dtos.service.UpdateTrainerDto;
import dev.branow.dtos.service.*;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainerMapper {

    private final TraineeTrainerMapper traineeTrainerMapper;
    private final TrainingTypeMapper trainingTypeMapper;
    private final TrainingMapper trainingMapper;

    public ShortTrainerDto toShortTrainerDto(Trainer trainer) {
        return traineeTrainerMapper.toShortTrainerDto(trainer);
    }

    public TrainerDto toTrainerDto(Trainer trainer) {
        return TrainerDto.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .username(trainer.getUsername())
                .password(trainer.getPassword())
                .isActive(trainer.getIsActive())
                .trainings(getTrainingDtos(trainer))
                .trainees(getShortTraineesDto(trainer))
                .specialization(trainingTypeMapper.toTrainingTypeDto(trainer.getSpecialization()))
                .build();
    }

    private List<ShortTraineeDto> getShortTraineesDto(Trainer trainer) {
        return Optional.ofNullable(trainer.getTrainings())
                .orElse(Collections.emptyList())
                .stream()
                .map(Training::getTrainee)
                .map(traineeTrainerMapper::toShortTraineeDto)
                .distinct()
                .toList();
    }

    private List<TrainingDto> getTrainingDtos(Trainer trainer) {
        return Optional.ofNullable(trainer.getTrainings())
                .orElse(Collections.emptyList())
                .stream()
                .map(trainingMapper::toTrainingDto)
                .toList();
    }

    public Trainer toTrainer(CreateTrainerDto dto) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        return trainer;
    }

}
