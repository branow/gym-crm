package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserDto {
    String username;
    String firstName;
    String lastName;
    Boolean isActive;
}
