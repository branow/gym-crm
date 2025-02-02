package dev.branow.cli;

import dev.branow.model.Trainer;
import dev.branow.model.TrainingType;
import dev.branow.services.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static dev.branow.cli.App.DASH;

@Component
@RequiredArgsConstructor
public class TrainerCommands {

    private static final Trainer DEF_TRAINER = new Trainer();

    static {
        DEF_TRAINER.setFirstName("John");
        DEF_TRAINER.setLastName("Smith");
        DEF_TRAINER.setSpecialization(TrainingType.CARDIO);
    }

    private final TrainerService trainerService;

    @Bean("trainer-get")
    public Command get() {
        return Command.builder()
                .key("ter-get")
                .usage("ter-get")
                .executor((_) -> {
                    var trainees = trainerService.getAll();
                    return trainees.stream()
                            .map(Trainer::toString)
                            .collect(Collectors.joining("\n"));
                })
                .build();
    }

    @Bean("trainer-create")
    public Command create() {
        return Command.builder()
                .key("ter-crt")
                .usage("ter-crt <first-name> <last-name> <specialization>")
                .executor((String[] args) -> {
                    if (args.length < 3) {
                        System.err.println("Not enough arguments: " + args.length);
                        return "@";
                    }
                    var firstName = args[1].equals(DASH) ? DEF_TRAINER.getFirstName() : args[1];
                    var lastName = args[2].equals(DASH) ? DEF_TRAINER.getLastName() : args[2];
                    TrainingType specialization = null;
                    if (args.length > 3) {
                        try {
                            specialization = args[3].equals(DASH) ? DEF_TRAINER.getSpecialization() : TrainingType.valueOf(args[3]);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid specialization: " + args[3]);
                            return "@";
                        }
                    }
                    var trainer = new Trainer();
                    trainer.setFirstName(firstName);
                    trainer.setLastName(lastName);
                    trainer.setSpecialization(specialization);
                    return trainerService.create(trainer).toString();
                })
                .build();
    }

    @Bean("trainer-update")
    public Command update() {
        return Command.builder()
                .key("ter-upt")
                .usage("ter-upt <id> <first-name> <last-name> <specialization>")
                .executor((String[] args) -> {
                    if (args.length < 5) {
                        System.err.println("Not enough arguments: " + args.length);
                        return "@";
                    }
                    var id = -1L;
                    try {
                        id = Long.parseLong(args[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid Id: " + args[1]);
                        return "@";
                    }
                    var firstName = args[2].equals(DASH) ? DEF_TRAINER.getFirstName() : args[2];
                    var lastName = args[3].equals(DASH) ? DEF_TRAINER.getLastName() : args[3];
                    TrainingType specialization;
                    try {
                        specialization = args[4].equals(DASH) ? DEF_TRAINER.getSpecialization() : TrainingType.valueOf(args[4]);
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid specialization: " + args[4]);
                        return "@";
                    }
                    var trainer = new Trainer();
                    trainer.setUserId(id);
                    trainer.setFirstName(firstName);
                    trainer.setLastName(lastName);
                    trainer.setSpecialization(specialization);
                    return trainerService.update(trainer).toString();
                })
                .build();
    }

}
