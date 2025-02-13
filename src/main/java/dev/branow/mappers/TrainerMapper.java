package dev.branow.mappers;

import dev.branow.dtos.CreateTrainerDto;
import dev.branow.dtos.TrainerDto;
import dev.branow.dtos.TrainingTitleDto;
import dev.branow.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainerMapper {

    private final TrainingTypeMapper trainingTypeMapper;
    private final TrainingMapper trainingMapper;

    public TrainerDto toTrainerDto(Trainer trainer) {
        return TrainerDto.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .username(trainer.getUsername())
                .trainings(getTrainingTitleDtos(trainer))
                .specialization(trainingTypeMapper.toTrainingTypeDto(trainer.getSpecialization()))
                .build();
    }

    private List<TrainingTitleDto> getTrainingTitleDtos(Trainer trainer) {
        return Optional.ofNullable(trainer.getTrainings())
                .orElse(Collections.emptyList())
                .stream()
                .map(trainingMapper::toTrainingTitleDto)
                .toList();
    }

    public Trainer toTrainer(CreateTrainerDto dto) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        return trainer;
    }

}
