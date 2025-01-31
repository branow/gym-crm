package dev.branow.services;

import dev.branow.model.Trainee;
import dev.branow.model.User;
import dev.branow.repositories.Repository;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainingRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeService extends UserService<Long, Trainee> {

    private final TrainingRepository trainingRepository;

    public TraineeService(
            TraineeRepository repository,
            TrainingRepository trainingRepository,
            List<Repository<Long, ? extends User>> repositories,
            PasswordGenerator passwordGenerator,
            UsernameGenerator usernameGenerator
    ) {
        super(repository, repositories, passwordGenerator, usernameGenerator);
        this.trainingRepository = trainingRepository;
    }

    public Trainee update(Trainee trainer) {
        return super.update(trainer.getUserId(), trainer);
    }

    @Override
    public void deleteById(Long id) {
        trainingRepository.deleteAllByTraineeId(id);
        super.deleteById(id);
    }

}
