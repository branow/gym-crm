package dev.branow.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingDto {
    Long id;
    String name;
    LocalDate date;
    Integer duration;
    TrainingTypeDto type;
    String trainee;
    String trainer;
}
