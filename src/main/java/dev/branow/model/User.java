package dev.branow.model;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    String firstName;
    String lastName;
    String username;
    String password;
    boolean isActive;
}
