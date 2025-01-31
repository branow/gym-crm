package dev.branow.cli;

import dev.branow.model.Trainee;
import dev.branow.services.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static dev.branow.cli.App.DASH;

@Component
@RequiredArgsConstructor
public class TraineeCommands {

    private static final Trainee DEF_TRAINEE = new Trainee();

    static {
        DEF_TRAINEE.setFirstName("John");
        DEF_TRAINEE.setLastName("Smith");
        DEF_TRAINEE.setAddress("456 Elm Street, Suite 3, Los Angeles, CA 90001, USA");
        DEF_TRAINEE.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    private final TraineeService traineeService;

    @Bean
    public Command teeGet() {
        return Command.builder()
                .key("tee-get")
                .usage("tee-get")
                .executor((_) -> {
                    var trainees = traineeService.getAll();
                    return trainees.stream()
                            .map(Trainee::toString)
                            .collect(Collectors.joining("\n"));
                })
                .build();
    }

    @Bean
    public Command teeCrt() {
        return Command.builder()
                .key("tee-crt")
                .usage("tee-crt <first-name> <last-name> <address?> <date-of-birth?>")
                .executor((String[] args) -> {
                    if (args.length < 3) {
                        System.err.println("Not enough arguments: " + args.length);
                        return "@";
                    }
                    var firstName = args[1].equals(DASH) ? DEF_TRAINEE.getFirstName() : args[1];
                    var lastName = args[2].equals(DASH) ? DEF_TRAINEE.getLastName() : args[2];
                    var address = args.length > 3 ? (args[3].equals(DASH) ? DEF_TRAINEE.getAddress() : args[3]) : null;
                    LocalDate dateOfBirth = null;
                    if (args.length > 4) {
                        try {
                            dateOfBirth = args[4].equals(DASH) ? DEF_TRAINEE.getDateOfBirth() : LocalDate.parse(args[4]);
                        } catch (DateTimeParseException e) {
                            System.err.println("Invalid date of birth: " + args[4]);
                            return "@";
                        }
                    }
                    var trainee = new Trainee();
                    trainee.setFirstName(firstName);
                    trainee.setLastName(lastName);
                    trainee.setAddress(address);
                    trainee.setDateOfBirth(dateOfBirth);
                    return traineeService.create(trainee).toString();
                })
                .build();
    }

    @Bean
    public Command teeUpt() {
        return Command.builder()
                .key("tee-upt")
                .usage("tee-upt <id> <first-name> <last-name> <address?> <date-of-birth?>")
                .executor((String[] args) -> {
                    if (args.length < 4) {
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
                    var firstName = args[2].equals(DASH) ? DEF_TRAINEE.getFirstName() : args[2];
                    var lastName = args[3].equals(DASH) ? DEF_TRAINEE.getLastName() : args[3];
                    var address = args.length > 4 ? (args[4].equals(DASH) ? DEF_TRAINEE.getAddress() : args[4]) : null;
                    LocalDate dateOfBirth = null;
                    if (args.length > 5) {
                        try {
                            dateOfBirth =  args[5].equals(DASH) ? DEF_TRAINEE.getDateOfBirth() : LocalDate.parse(args[5]);
                        } catch (DateTimeParseException e) {
                            System.err.println("Invalid date of birth: " + args[5]);
                            return "@";
                        }
                    }
                    var trainee = new Trainee();
                    trainee.setUserId(id);
                    trainee.setFirstName(firstName);
                    trainee.setLastName(lastName);
                    trainee.setAddress(address);
                    trainee.setDateOfBirth(dateOfBirth);
                    return traineeService.update(trainee).toString();
                })
                .build();
    }

    @Bean
    public Command teeDel() {
        return Command.builder()
                .key("tee-del")
                .usage("tee-del <id>")
                .executor((String[] args) -> {
                    if (args.length < 2) {
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
                    traineeService.deleteById(id);
                    return "";

                })
                .build();
    }}
