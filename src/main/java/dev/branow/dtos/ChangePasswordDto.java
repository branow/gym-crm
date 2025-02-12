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
public class ChangePasswordDto {
    @NotNull
    String oldPassword;
    @NotBlank @Size(min = 8, max = 60)
    String newPassword;
    @NotBlank @Size(min = 8, max = 60)
    String confirmPassword;
}
