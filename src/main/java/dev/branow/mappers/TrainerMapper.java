package dev.branow.mappers;

import dev.branow.dtos.request.CreateTrainerRequest;
import dev.branow.dtos.service.CreateTrainerDto;
import dev.branow.dtos.service.UpdateTrainerDto;
import dev.branow.dtos.request.UpdateTrainerRequest;
import dev.branow.dtos.service.*;
import dev.branow.dtos.response.TrainerResponse;
import dev.branow.dtos.response.CredentialsResponse;
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

    public CreateTrainerDto toCreateTrainerDto(CreateTrainerRequest request) {
        return CreateTrainerDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .build();
    }

    public CriteriaTrainingTrainerDto toCriteriaTrainingTrainerDto(
            String username, LocalDate from, LocalDate to, String trainee
    ) {
        return CriteriaTrainingTrainerDto.builder()
                .trainerUsername(username)
                .from(from)
                .to(to)
                .traineeUsername(trainee)
                .build();
    }

    public ShortTrainerDto toShortTrainerDto(Trainer trainer) {
        return traineeTrainerMapper.toShortTrainerDto(trainer);
    }

    public UpdateTrainerDto toUpdateTraineeDto(String username, UpdateTrainerRequest request) {
        var trainer = new UpdateTrainerDto();
        trainer.setUsername(username);
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setIsActive(request.getIsActive());
        trainer.setSpecialization(request.getSpecialization());
        return trainer;
    }

    public TrainerResponse toTrainerResponse(TrainerDto dto) {
        return TrainerResponse.builder()
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .specialization(dto.getSpecialization().getId())
                .isActive(dto.getIsActive())
                .favoriteBy(dto.getFavoriteBy())
                .build();
    }

    public CredentialsResponse toCredentialsResponse(TrainerDto dto) {
        return CredentialsResponse.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
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
                .favoriteBy(getShortFavoriteByDtos(trainer))
                .specialization(trainingTypeMapper.toTrainingTypeDto(trainer.getSpecialization()))
                .build();
    }

    private List<ShortTraineeDto> getShortFavoriteByDtos(Trainer trainer) {
        return Optional.ofNullable(trainer.getFavoriteBy())
                .orElse(Collections.emptyList())
                .stream()
                .map(traineeTrainerMapper::toShortTraineeDto)
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
