package dev.branow.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTrainerRequest {

    @NotBlank
    @Size(min = 2, max = 45)
    String firstName;

    @NotBlank @Size(min = 2, max = 45)
    String lastName;

    @NotNull
    @PositiveOrZero
    Long specialization;

}
