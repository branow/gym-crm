package dev.branow.cli;

import dev.branow.model.Training;
import dev.branow.model.TrainingType;
import dev.branow.services.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static dev.branow.cli.App.DASH;

@Component
@RequiredArgsConstructor
public class TrainingCommands {

    private final TrainingService trainingService;

    private static final Training DEF_TRAINING = Training.builder()
            .trainingName("Cardio A5")
            .trainingType(TrainingType.CARDIO)
            .startTime(LocalDateTime.of(2024, 2, 5, 12, 0))
            .duration(Duration.ofMinutes(90))
            .build();

    @Bean
    public Command tngGet() {
        return Command.builder()
                .key("tng-get")
                .usage("tng-get")
                .executor((_) -> {
                    var trainees = trainingService.getAll();
                    return trainees.stream()
                            .map(Training::toString)
                            .collect(Collectors.joining("\n"));
                })
                .build();
    }

    @Bean
    public Command tngCrt() {
        return Command.builder()
                .key("tng-crt")
                .usage("tng-crt <trainee-id> <trainer-id> <name> <type> <time> <duration>")
                .executor((String[] args) -> {
                    if (args.length < 6) {
                        System.err.println("Not enough arguments: " + args.length);
                        return "@";
                    }
                    var traineeId = -1L;
                    try {
                        traineeId = Long.parseLong(args[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid trainee Id: " + args[1]);
                        return "@";
                    }

                    var trainerId = -1L;
                    try {
                        trainerId = Long.parseLong(args[2]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid trainer Id: " + args[2]);
                        return "@";
                    }

                    var name = args[3].equals(DASH) ? DEF_TRAINING.getTrainingName() : args[3];
                    TrainingType type;
                    try {
                        type = args[4].equals(DASH) ? DEF_TRAINING.getTrainingType() : TrainingType.valueOf(args[4]);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid type: " + args[4]);
                        return "@";
                    }

                    LocalDateTime time;
                    try {
                        time = args[5].equals(DASH) ? DEF_TRAINING.getStartTime() : LocalDateTime.parse(args[5]);
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid time: " + args[5]);
                        return "@";
                    }

                    Duration duration;
                    try {
                        duration = args[6].equals(DASH) ? DEF_TRAINING.getDuration() : Duration.parse(args[6]);
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid duration: " + args[6]);
                        return "@";
                    }

                    var training = Training.builder()
                            .traineeId(traineeId)
                            .trainerId(trainerId)
                            .trainingName(name)
                            .trainingType(type)
                            .startTime(time)
                            .duration(duration)
                            .build();
                    return trainingService.create(training).toString();
                })
                .build();


    }

}
