package dev.branow.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.branow.dtos.service.ShortTrainerDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeResponse {
    String username;
    String firstName;
    String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;
    String address;
    Boolean isActive;
    List<ShortTrainerDto> trainers;
}
