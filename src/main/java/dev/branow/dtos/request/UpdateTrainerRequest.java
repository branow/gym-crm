package dev.branow.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTrainerRequest {

    @NotBlank
    @Size(min = 2, max = 45)
    String firstName;

    @NotBlank
    @Size(min = 2, max = 45)
    String lastName;

    @NotNull
    @PositiveOrZero
    Long specialization;

    @NotNull
    Boolean isActive;

}
