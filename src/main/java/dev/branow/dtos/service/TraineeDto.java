package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeDto {
    Long id;
    String firstName;
    String lastName;
    String username;
    String password;
    String address;
    LocalDate dateOfBirth;
    Boolean isActive;
    List<TrainingDto> trainings;
    List<ShortTrainerDto> favouriteTrainers;
    List<ShortTrainerDto> trainers;
}
