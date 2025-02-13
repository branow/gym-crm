package dev.branow.mappers;

import dev.branow.dtos.CreateTrainingDto;
import dev.branow.dtos.TrainingDto;
import dev.branow.dtos.TrainingTitleDto;
import dev.branow.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainingMapper {

    private final TrainingTypeMapper trainingTypeMapper;

    public TrainingDto toTrainingDto(Training training) {
        var type = training.getType() != null ? trainingTypeMapper.toTrainingTypeDto(training.getType()) : null;
        var trainee = training.getTrainee() != null ? training.getTrainee().getUsername() : null;
        var trainer = training.getTrainer() != null ? training.getTrainer().getUsername() : null;
        return TrainingDto.builder()
                .id(training.getId())
                .name(training.getName())
                .type(type)
                .date(training.getDate())
                .trainee(trainee)
                .trainer(trainer)
                .duration(training.getDuration())
                .build();
    }

    public TrainingTitleDto toTrainingTitleDto(Training training) {
        return TrainingTitleDto.builder()
                .id(training.getId())
                .name(training.getName())
                .build();
    }

    public Training toTraining(CreateTrainingDto dto) {
        return Training.builder()
                .name(dto.getName())
                .date(dto.getDate())
                .duration(dto.getDuration())
                .build();
    }
}
