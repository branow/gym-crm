package dev.branow.mappers;

import dev.branow.dtos.service.ShortTraineeDto;
import dev.branow.dtos.service.ShortTrainerDto;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TraineeTrainerMapper {

    public ShortTrainerDto toShortTrainerDto(Trainer trainer) {
        return ShortTrainerDto.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .username(trainer.getUsername())
                .specialization(trainer.getSpecialization().getId())
                .build();
    }

    public ShortTraineeDto toShortTraineeDto(Trainee trainee) {
        return ShortTraineeDto.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .build();
    }

}
