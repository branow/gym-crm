package dev.branow;

import dev.branow.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class TestDataFactory {

    private static final Random RANDOM = new Random();

    private static int nextInt() {
        return RANDOM.nextInt(1000);
    }

    public static User nextUser() {
        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe" + nextInt())
                .password("password")
                .isActive(true)
                .build();
    }

    public static Trainee nextTrainee(List<Training> trainings) {
        var user = nextUser();
        Trainee trainee = new Trainee();
        trainee.setFirstName(user.getFirstName());
        trainee.setLastName(user.getLastName());
        trainee.setUsername(user.getUsername());
        trainee.setPassword(user.getPassword());
        trainee.setIsActive(user.getIsActive());
        trainee.setDateOfBirth(LocalDate.of(1990, 5, 15));
        trainee.setAddress("123 Main St, City A");
        trainee.setTrainings(trainings);
        return trainee;
    }

    public static Trainer nextTrainer(TrainingType specialization, List<Training> trainings) {
        var user = nextUser();
        var trainer = new Trainer();
        trainer.setFirstName(user.getFirstName());
        trainer.setLastName(user.getLastName());
        trainer.setUsername(user.getUsername());
        trainer.setPassword(user.getPassword());
        trainer.setIsActive(user.getIsActive());
        trainer.setSpecialization(specialization);
        trainer.setTrainings(trainings);
        return trainer;
    }

    public static Training nextTraining(TrainingType type, Trainee trainee, Trainer trainer) {
        return Training.builder()
                .name("Full Body Strength")
                .date(LocalDate.of(2024, 2, 10))
                .duration(60)
                .trainee(trainee)
                .trainer(trainer)
                .type(type)
                .build();
    }

    public static TrainingType nextTrainingType() {
        return TrainingType.builder()
                .id((long) nextInt())
                .name("Strength Training" + nextInt())
                .build();
    }

    public static User clone(User user) {
        return User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .build();
    }

    public static Trainee clone(Trainee trainee) {
        Trainee clone = new Trainee();
        clone.setId(trainee.getId());
        clone.setFirstName(trainee.getFirstName());
        clone.setLastName(trainee.getLastName());
        clone.setUsername(trainee.getUsername());
        clone.setPassword(trainee.getPassword());
        clone.setIsActive(trainee.getIsActive());
        clone.setDateOfBirth(trainee.getDateOfBirth());
        clone.setAddress(trainee.getAddress());
        clone.setTrainings(trainee.getTrainings());
        return clone;
    }

    public static Trainer clone(Trainer trainer) {
        Trainer clone = new Trainer();
        clone.setId(trainer.getId());
        clone.setFirstName(trainer.getFirstName());
        clone.setLastName(trainer.getLastName());
        clone.setUsername(trainer.getUsername());
        clone.setPassword(trainer.getPassword());
        clone.setIsActive(trainer.getIsActive());
        clone.setSpecialization(trainer.getSpecialization());
        clone.setTrainings(trainer.getTrainings());
        return clone;
    }

    public static Training clone(Training training) {
        Training clone = new Training();
        clone.setId(training.getId());
        clone.setName(training.getName());
        clone.setType(training.getType());
        clone.setTrainer(training.getTrainer());
        clone.setTrainee(training.getTrainee());
        clone.setDate(training.getDate());
        clone.setDuration(training.getDuration());
        return clone;
    }

    public static TrainingType clone(TrainingType type) {
        return TrainingType.builder()
                .id(type.getId())
                .name(type.getName())
                .build();
    }


}
