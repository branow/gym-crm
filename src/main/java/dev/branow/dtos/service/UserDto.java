package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    String firstName;
    String lastName;
    String username;
    String password;
    Boolean isActive;
}
