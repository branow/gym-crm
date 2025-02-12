package dev.branow.mappers;

import dev.branow.dtos.CreateTrainingDto;
import dev.branow.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public Training toTraining(CreateTrainingDto dto) {
        return Training.builder()
                .name(dto.getName())
                .date(dto.getDate())
                .duration(dto.getDuration())
                .build();
    }
}
