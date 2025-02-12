package dev.branow.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserDto {
    @NotNull
    Long id;
    @NotBlank @Size(min = 2, max = 45)
    String firstName;
    @NotBlank @Size(min = 2, max = 45)
    String lastName;
}
