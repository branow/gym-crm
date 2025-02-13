package dev.branow.dtos;

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
    TrainingTypeDto specialization;
    List<TrainingTitleDto> trainings;
}
