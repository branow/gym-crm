package dev.branow.cli;

import dev.branow.dtos.CreateTraineeDto;
import dev.branow.dtos.UpdateTraineeDto;
import dev.branow.services.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TraineeCommands {

    private static final CreateTraineeDto DEF_TRAINEE = new CreateTraineeDto();

    static {
        DEF_TRAINEE.setFirstName("Bob");
        DEF_TRAINEE.setLastName("Doe");
        DEF_TRAINEE.setAddress("456 Elm Street, Suite 3, Los Angeles, CA 90001, USA");
        DEF_TRAINEE.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    private final TraineeService traineeService;

    @Bean("getTrainee")
    public Command get() {
        return Command.builder()
                .key("get-tee")
                .description("Get Trainee by Username")
                .usage("get-tee <username!>")
                .executor((args) -> {
                    var parser = ArgsParser.of(args);
                    var username = parser.parse(1, String.class).get();
                    return traineeService.getByUsername(username).toString();
                })
                .build();
    }

    @Bean("createTrainee")
    public Command create() {
        return Command.builder()
                .key("crt-tee")
                .description("Create Trainee")
                .usage("crt-tee <first-name> <last-name> -a <address?> -d <date-of-birth?>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var trainee = new CreateTraineeDto();
                    trainee.setFirstName(parser.parse(1, String.class).orDefault(DEF_TRAINEE.getFirstName()));
                    trainee.setLastName(parser.parse(2, String.class).orDefault(DEF_TRAINEE.getLastName()));
                    trainee.setAddress(parser.parse("a", String.class).orElse(DEF_TRAINEE.getAddress(), null));
                    trainee.setDateOfBirth(parser.parse("d", LocalDate.class).orElse(DEF_TRAINEE.getDateOfBirth(), null));
                    return traineeService.create(trainee).toString();
                })
                .build();
    }

    @Bean("updateTrainee")
    public Command update() {
        return Command.builder()
                .key("upt-tee")
                .description("Update Trainee")
                .usage("upt-tee <id!> <first-name> <last-name> -a <address?> -d <date-of-birth?>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var trainee = new UpdateTraineeDto();
                    trainee.setId(parser.parse(1, Long.class).get());
                    trainee.setFirstName(parser.parse(2, String.class).orDefault(DEF_TRAINEE.getFirstName()));
                    trainee.setLastName(parser.parse(3, String.class).orDefault(DEF_TRAINEE.getLastName()));
                    trainee.setAddress(parser.parse("a", String.class).orElse(DEF_TRAINEE.getAddress(), null));
                    trainee.setDateOfBirth(parser.parse("d", LocalDate.class).orElse(DEF_TRAINEE.getDateOfBirth(), null));
                    return traineeService.update(trainee).toString();
                })
                .build();
    }

    @Bean("deleteTrainee")
    public Command delete() {
        return Command.builder()
                .key("del-tee")
                .description("Delete Trainee by Username")
                .usage("del-tee <username!>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var username = parser.parse(1, String.class).get();
                    traineeService.deleteByUsername(username);
                    return "";
                })
                .build();
    }

    @Bean("addFavoriteTrainer")
    public Command addFavoriteTrainer() {
        return Command.builder()
                .key("add-fav-ter")
                .description("Add Favorite Trainer")
                .usage("add-fav-ter <trainee-username!> <trainer-username!>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var trainee = parser.parse(1, String.class).get();
                    var trainer = parser.parse(2, String.class).get();
                    return traineeService.addFavoriteTrainer(trainee, trainer).toString();
                })
                .build();
    }

    @Bean("deleteFavoriteTrainer")
    public Command deleteFavoriteTrainer() {
        return Command.builder()
                .key("del-fav-ter")
                .description("Delete Favorite Trainer")
                .usage("del-fav-ter <trainee-username!> <trainer-username!>")
                .executor((String[] args) -> {
                    var parser = ArgsParser.of(args);
                    var trainee = parser.parse(1, String.class).get();
                    var trainer = parser.parse(2, String.class).get();
                    return traineeService.deleteFavoriteTrainer(trainee, trainer).toString();
                })
                .build();
    }
}
