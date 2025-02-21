package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTrainingDto {
    String name;
    LocalDate date;
    Integer duration;
    String trainee;
    String trainer;
}
