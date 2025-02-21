package dev.branow.dtos.service;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CriteriaTrainingTraineeDto {
    @NotBlank
    String traineeUsername;
    String trainerUsername;
    LocalDate from;
    LocalDate to;
    Long typeId;
}
