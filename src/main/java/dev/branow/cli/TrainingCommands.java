package dev.branow.cli;

import dev.branow.dtos.CreateTrainingDto;
import dev.branow.dtos.CriteriaTrainingTraineeDto;
import dev.branow.dtos.CriteriaTrainingTrainerDto;
import dev.branow.model.Training;
import dev.branow.services.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainingCommands {

    private final TrainingService service;

    private static final CreateTrainingDto DEF_TRAINING = CreateTrainingDto.builder()
            .name("Cardio A5")
            .typeId(1L)
            .date(LocalDate.of(2024, 2, 15))
            .duration(90)
            .build();

    @Bean("getTrainingByTrainee")
    public Command getAllByTrainee() {
        return Command.builder()
                .key("get-tng-tee")
                .description("Get Trainings By Trainee's Username")
                .usage("get-tng-tee <username!> -ter <trainer?> -from <from?> -to <to?> -type <typeId?> ")
                .executor((args) -> {
                    var parser = ArgsParser.of(args);
                    var dto = new CriteriaTrainingTraineeDto();
                    dto.setTraineeUsername(parser.parse(1, String.class).get());
                    dto.setTrainerUsername(parser.parse("ter", String.class).orElse("Emma.Wilson", null));
                    dto.setFrom(parser.parse("from", LocalDate.class).orElse(DEF_TRAINING.getDate(), null));
                    dto.setTo(parser.parse("to", LocalDate.class).orElse(DEF_TRAINING.getDate(), null));
                    dto.setTypeId(parser.parse("type", Long.class).orElse(DEF_TRAINING.getTypeId(), null));
                    return service.getAllByTraineeUsernameCriteria(dto)
                            .stream()
                            .map(Training::toString)
                            .collect(Collectors.joining("\n"));
                })
                .build();
    }

    @Bean("getTrainingByTrainer")
    public Command getAllByTrainer() {
        return Command.builder()
                .key("get-tng-ter")
                .description("Get Trainings By Trainer's Username")
                .usage("get-tng-ter <username!> -tee <trainee?> -from <from?> -to <to?> -type <typeId?> ")
                .executor((args) -> {
                    var parser = ArgsParser.of(args);
                    var dto = new CriteriaTrainingTrainerDto();
                    dto.setTrainerUsername(parser.parse(1, String.class).get());
                    dto.setTraineeUsername(parser.parse("tee", String.class).orElse("John.Doe", null));
                    dto.setFrom(parser.parse("from", LocalDate.class).orElse(DEF_TRAINING.getDate(), null));
                    dto.setTo(parser.parse("to", LocalDate.class).orElse(DEF_TRAINING.getDate(), null));
                    return service.getAllByTrainerUsernameCriteria(dto)
                            .stream()
                            .map(Training::toString)
                            .collect(Collectors.joining("\n"));
                })
                .build();
    }

    @Bean("createTraining")
    public Command create() {
        return Command.builder()
                .key("crt-tng")
                .description("Create Training")
                .usage("crt-tng <trainee-id!> <trainer-id!> <name> <type> <date> <duration>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var training = new CreateTrainingDto();
                    training.setTraineeId(parser.parse(1, Long.class).get());
                    training.setTrainerId(parser.parse(2, Long.class).get());
                    training.setName(parser.parse(3, String.class).orDefault(DEF_TRAINING.getName()));
                    training.setTypeId(parser.parse(4, Long.class).orDefault(DEF_TRAINING.getTypeId()));
                    training.setDate(parser.parse(5, LocalDate.class).orDefault(DEF_TRAINING.getDate()));
                    training.setDuration(parser.parse(6, Integer.class).orDefault(DEF_TRAINING.getDuration()));
                    return service.create(training).toString();
                })
                .build();
    }

}
