package dev.branow.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    List<TrainingDto> trainings;
    List<TrainerDto> favouriteTrainers;
}
