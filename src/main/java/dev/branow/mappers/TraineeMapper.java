package dev.branow.mappers;

import dev.branow.dtos.request.CreateTraineeRequest;
import dev.branow.dtos.request.UpdateFavoriteTrainersRequest;
import dev.branow.dtos.request.UpdateTraineeRequest;
import dev.branow.dtos.service.*;
import dev.branow.dtos.response.TraineeResponse;
import dev.branow.dtos.response.CredentialsResponse;
import dev.branow.model.Trainee;
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

    public CreateTraineeDto toCreateTraineeDto(CreateTraineeRequest request) {
        return CreateTraineeDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .build();
    }

    public CriteriaTrainingTraineeDto toCriteriaTrainingTraineeDto(
            String username, LocalDate from, LocalDate to, String trainer, Long type
    ) {
        return CriteriaTrainingTraineeDto.builder()
                .from(from)
                .to(to)
                .traineeUsername(username)
                .trainerUsername(trainer)
                .typeId(type)
                .build();
    }

    public UpdateFavoriteTrainersDto toUpdateFavouriteTrainersDto(
            String username,
            UpdateFavoriteTrainersRequest request
    ) {
        return UpdateFavoriteTrainersDto.builder()
                .trainee(username)
                .trainers(request.getTrainers())
                .build();
    }

    public UpdateTraineeDto toUpdateTraineeDto(String username, UpdateTraineeRequest request) {
        var trainee = new UpdateTraineeDto();
        trainee.setUsername(username);
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setAddress(request.getAddress());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setIsActive(request.getIsActive());
        return trainee;
    }

    public TraineeResponse toTraineeResponse(TraineeDto trainee) {
        return TraineeResponse.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getIsActive())
                .favoriteTrainers(trainee.getFavouriteTrainers())
                .build();
    }

    public CredentialsResponse toCredentialsResponse(TraineeDto dto) {
        return CredentialsResponse.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
    }

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
                .build();
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
