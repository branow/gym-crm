package dev.branow.mappers;

import dev.branow.dtos.CreateTraineeDto;
import dev.branow.dtos.TraineeDto;
import dev.branow.dtos.TrainingTitleDto;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TraineeMapper {

    private final TrainingMapper trainingMapper;

    public TraineeDto toTraineeDto(Trainee trainee) {
        return TraineeDto.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .trainings(getTrainingTitleDtos(trainee))
                .favouriteTrainers(getTrainerUsernames(trainee))
                .build();
    }

    private List<TrainingTitleDto> getTrainingTitleDtos(Trainee trainee) {
        return Optional.ofNullable(trainee.getTrainings())
                .orElse(Collections.emptyList())
                .stream()
                .map(trainingMapper::toTrainingTitleDto)
                .toList();
    }

    private List<String> getTrainerUsernames(Trainee trainee) {
        return Optional.ofNullable(trainee.getTrainers())
                .orElse(Collections.emptyList())
                .stream()
                .map(Trainer::getUsername)
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
