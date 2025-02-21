package dev.branow.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTraineeRequest {

    @NotBlank
    @Size(min = 2, max = 45)
    String firstName;

    @NotBlank
    @Size(min = 2, max = 45)
    String lastName;

    @NotNull
    Boolean isActive;

    @Past
    LocalDate dateOfBirth;

    @Size(max = 255)
    String address;

}
