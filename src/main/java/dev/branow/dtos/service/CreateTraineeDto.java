package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTraineeDto {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String address;
}
