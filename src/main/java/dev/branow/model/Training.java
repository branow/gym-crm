package dev.branow.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Training {
    Long trainingId;
    Long traineeId;
    Long trainerId;
    String trainingName;
    TrainingType trainingType;
    LocalDateTime startTime;
    Duration duration;
}
