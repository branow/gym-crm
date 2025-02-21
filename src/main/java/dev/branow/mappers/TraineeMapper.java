package dev.branow.mappers;

import dev.branow.dtos.service.*;
import dev.branow.model.Trainee;
import dev.branow.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TraineeMapper {

    private final TrainingMapper trainingMapper;
    private final TraineeTrainerMapper traineeTrainerMapper;

    public TraineeDto toTraineeDto(Trainee trainee) {
        return TraineeDto.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .password(trainee.getPassword())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .isActive(trainee.getIsActive())
                .trainings(getTrainingDtos(trainee))
                .favouriteTrainers(getShortFavoriteTrainersDtos(trainee))
                .trainers(getShortTrainerDtos(trainee))
                .build();
    }
    private List<ShortTrainerDto> getShortTrainerDtos(Trainee trainee) {
        return Optional.ofNullable(trainee.getTrainings())
                .orElse(Collections.emptyList()).stream()
                .map(Training::getTrainer)
                .map(traineeTrainerMapper::toShortTrainerDto)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ShortTrainerDto> getShortFavoriteTrainersDtos(Trainee trainee) {
        return Optional.ofNullable(trainee.getFavoriteTrainers())
                .orElse(Collections.emptyList()).stream()
                .map(traineeTrainerMapper::toShortTrainerDto)
                .collect(Collectors.toList());
    }

    private List<TrainingDto> getTrainingDtos(Trainee trainee) {
        return Optional.ofNullable(trainee.getTrainings())
                .orElse(Collections.emptyList()).stream()
                .map(trainingMapper::toTrainingDto)
                .toList();
    }

    public Trainee toTrainee(CreateTraineeDto dto) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        return trainee;
    }

}
