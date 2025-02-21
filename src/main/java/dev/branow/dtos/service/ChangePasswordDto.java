package dev.branow.dtos.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordDto {
    @NotBlank
    String oldPassword;
    @NotBlank @Size(min = 8, max = 20)
    String newPassword;
}
