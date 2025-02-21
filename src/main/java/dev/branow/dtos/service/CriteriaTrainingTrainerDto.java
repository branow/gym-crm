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
public class CriteriaTrainingTrainerDto {
    @NotBlank
    String trainerUsername;
    String traineeUsername;
    LocalDate from;
    LocalDate to;
}

