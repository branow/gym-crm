package dev.branow.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTraineeDto {
    @NotBlank @Size(min = 2, max = 45)
    String firstName;
    @NotBlank @Size(min = 2, max = 45)
    String lastName;
    LocalDate dateOfBirth;
    @Size(max = 255)
    String address;
}
