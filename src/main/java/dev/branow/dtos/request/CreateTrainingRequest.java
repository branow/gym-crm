package dev.branow.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTrainingRequest {

    @NotBlank
    String trainee;

    @NotBlank
    String trainer;

    @NotBlank
    @Size(min = 2, max = 100)
    String name;

    @NotNull
    LocalDate date;

    @NotNull
    @Positive
    Integer duration;

}
