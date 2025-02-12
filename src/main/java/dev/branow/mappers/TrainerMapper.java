package dev.branow.mappers;

import dev.branow.dtos.CreateTrainerDto;
import dev.branow.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public Trainer toTrainer(CreateTrainerDto dto) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        return trainer;
    }

}
