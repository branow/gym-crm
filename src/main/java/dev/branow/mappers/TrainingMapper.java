package dev.branow.mappers;

import dev.branow.dtos.request.CreateTrainingRequest;
import dev.branow.dtos.service.CreateTrainingDto;
import dev.branow.dtos.response.TrainingResponse;
import dev.branow.dtos.service.TrainingDto;
import dev.branow.dtos.service.TrainingTypeDto;
import dev.branow.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainingMapper {

    private final TrainingTypeMapper trainingTypeMapper;

    public CreateTrainingDto toCreateTrainingDto(CreateTrainingRequest request) {
        return CreateTrainingDto.builder()
                .date(request.getDate())
                .duration(request.getDuration())
                .name(request.getName())
                .trainer(request.getTrainer())
                .trainee(request.getTrainee())
                .build();
    }

    public TrainingResponse toTrainingResponse(TrainingDto dto) {
        return TrainingResponse.builder()
                .trainer(dto.getTrainer())
                .trainee(dto.getTrainee())
                .date(dto.getDate())
                .duration(dto.getDuration())
                .name(dto.getName())
                .type(Optional.ofNullable(dto.getType()).map(TrainingTypeDto::getName).orElse(null))
                .build();
    }

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

    public Training toTraining(CreateTrainingDto dto) {
        return Training.builder()
                .name(dto.getName())
                .date(dto.getDate())
                .duration(dto.getDuration())
                .build();
    }
}
