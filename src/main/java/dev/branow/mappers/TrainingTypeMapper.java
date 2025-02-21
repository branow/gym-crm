package dev.branow.mappers;

import dev.branow.dtos.service.TrainingTypeDto;
import dev.branow.model.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeMapper {

    public TrainingTypeDto toTrainingTypeDto(TrainingType trainingType) {
        return TrainingTypeDto.builder()
                .id(trainingType.getId())
                .name(trainingType.getName())
                .build();
    }

}
