package dev.branow.dtos.response;

import dev.branow.dtos.service.ShortTraineeDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerResponse {
    String username;
    String firstName;
    String lastName;
    Long specialization;
    Boolean isActive;
    List<ShortTraineeDto> trainees;
}
