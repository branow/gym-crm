package dev.branow.mappers;

import dev.branow.dtos.response.TrainingTypeResponse;
import dev.branow.dtos.service.TrainingTypeDto;
import dev.branow.model.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeMapper {

    public TrainingTypeResponse mapTrainingTypeResponse(TrainingTypeDto dto) {
        return TrainingTypeResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public TrainingTypeDto mapTrainingTypeDto(TrainingType trainingType) {
        return TrainingTypeDto.builder()
                .id(trainingType.getId())
                .name(trainingType.getName())
                .build();
    }

}
