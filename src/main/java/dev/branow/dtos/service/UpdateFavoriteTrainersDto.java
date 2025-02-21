package dev.branow.dtos.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFavoriteTrainersDto {
    String trainee;
    List<String> trainers;
}
