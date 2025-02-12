package dev.branow.cli;

import dev.branow.dtos.CreateTrainerDto;
import dev.branow.dtos.UpdateTrainerDto;
import dev.branow.model.Trainer;
import dev.branow.services.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainerCommands {

    private static final CreateTrainerDto DEF_TRAINER = new CreateTrainerDto();

    static {
        DEF_TRAINER.setFirstName("John");
        DEF_TRAINER.setLastName("Smith");
        DEF_TRAINER.setSpecialization(1L);
    }

    private final TrainerService trainerService;

    @Bean("getTrainer")
    public Command get() {
        return Command.builder()
                .key("get-ter")
                .description("Get Trainer by Username")
                .usage("get-ter <username!>")
                .executor((args) -> {
                    var parser = ArgsParser.of(args);
                    var username = parser.parse(1, String.class).get();
                    return trainerService.getByUsername(username).toString();
                })
                .build();
    }

    @Bean("getTrainerNotAssigned")
    public Command getAllNotAssigned() {
        return Command.builder()
                .key("get-ter-na")
                .description("Get Not Assigned Trainers on Trainee by Trainee's Username")
                .usage("get-ter-na <username!>")
                .executor((args) -> {
                    var parser = ArgsParser.of(args);
                    var username = parser.parse(1, String.class).get();
                    return trainerService.getAllNotAssignedOnTraineeByTraineeUsername(username).stream()
                            .map(Trainer::toString)
                            .collect(Collectors.joining("\n"));
                })
                .build();
    }

    @Bean("createTrainer")
    public Command create() {
        return Command.builder()
                .key("crt-ter")
                .description("Create Trainer")
                .usage("crt-ter <first-name> <last-name> <specialization>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var trainer = new CreateTrainerDto();
                    trainer.setFirstName(parser.parse(1, String.class).orDefault(DEF_TRAINER.getFirstName()));
                    trainer.setLastName(parser.parse(2, String.class).orDefault(DEF_TRAINER.getLastName()));
                    trainer.setSpecialization(parser.parse(3, Long.class).orDefault(DEF_TRAINER.getSpecialization()));
                    return trainerService.create(trainer).toString();
                })
                .build();
    }

    @Bean("updateTrainer")
    public Command update() {
        return Command.builder()
                .key("upt-ter")
                .description("Update Trainer")
                .usage("upt-ter <id!> <first-name> <last-name> <specialization>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var trainer = new UpdateTrainerDto();
                    trainer.setId(parser.parse(1, Long.class).get());
                    trainer.setFirstName(parser.parse(2, String.class).orDefault(DEF_TRAINER.getFirstName()));
                    trainer.setLastName(parser.parse(3, String.class).orDefault(DEF_TRAINER.getLastName()));
                    trainer.setSpecialization(parser.parse(4, Long.class).orDefault(DEF_TRAINER.getSpecialization()));
                    return trainerService.update(trainer).toString();
                })
                .build();
    }

}
