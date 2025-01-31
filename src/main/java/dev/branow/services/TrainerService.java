package dev.branow.services;

import dev.branow.model.Trainer;
import dev.branow.model.User;
import dev.branow.repositories.Repository;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService extends UserService<Long, Trainer> {

    private final TrainingRepository trainingRepository;

    public TrainerService(
            TrainerRepository repository,
            TrainingRepository trainingRepository,
            List<Repository<Long, ? extends User>> repositories,
            PasswordGenerator passwordGenerator,
            UsernameGenerator usernameGenerator
    ) {
        super(repository, repositories, passwordGenerator, usernameGenerator);
        this.trainingRepository = trainingRepository;
    }

    public Trainer update(Trainer trainer) {
        return super.update(trainer.getUserId(), trainer);
    }

    @Override
    public void deleteById(Long id) {
        trainingRepository.deleteAllByTrainerId(id);
        super.deleteById(id);
    }

}
