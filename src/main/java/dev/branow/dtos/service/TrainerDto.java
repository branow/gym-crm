package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerDto {
    Long id;
    String firstName;
    String lastName;
    String username;
    String password;
    Boolean isActive;
    TrainingTypeDto specialization;
    List<TrainingDto> trainings;
    List<ShortTraineeDto> trainees;
}
