package dev.branow.mappers;

import dev.branow.dtos.CreateTraineeDto;
import dev.branow.model.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    public Trainee toTrainee(CreateTraineeDto dto) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        return trainee;
    }

}
