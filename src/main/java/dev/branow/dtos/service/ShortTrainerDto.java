package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortTrainerDto {
    String username;
    String firstName;
    String lastName;
    Long specialization;
}
