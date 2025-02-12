package dev.branow.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import static dev.branow.cli.Color.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class App {

    public static final String DASH = "-";
    private static final String EXIT_COMMAND = "exit";
    private static final String HELP_COMMAND = "help";
    private static final String NO_COMMAND_MESSAGE = "No command specified";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command";

    private final List<Command> commands;

    private boolean isRunning = true;

    public void start() {
        var caret = colorize("> ", BLUE);
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(help().execute());
            System.out.print(caret);

            while (isRunning && scanner.hasNextLine()) {
                processInput(scanner.nextLine());
                System.out.print(caret);
            }
        }
    }

    private void processInput(String line) {
        if (line.isEmpty()) {
            System.out.println(colorize(NO_COMMAND_MESSAGE, RED));
            return;
        }

        var args = Arrays.stream(line.split(" "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        var key = args[0];
        if (EXIT_COMMAND.equals(key)) {
            isRunning = false;
            return;
        }

        var command = findCommand(key);
        if (command.isEmpty()) {
            System.out.println(colorize(UNKNOWN_COMMAND_MESSAGE + " : " + key, RED));
            return;
        }

        executeCommand(command.get(), args);
    }

    private Optional<Command> findCommand(String key) {
        if (HELP_COMMAND.equals(key)) {
            return Optional.of(help());
        }
        return commands.stream()
                .filter(cmd -> cmd.key().equals(key))
                .findFirst();
    }

    private void executeCommand(Command command, String[] args) {
        try {
            String response = command.execute(args);
            System.out.println(response);
        } catch (Exception e) {
            log.error("Error executing command: {}", e.getMessage());
        }
    }

    public Command help() {
        return Command.builder()
                .key("help")
                .usage("help")
                .executor(_ ->
                        commands.stream()
                                .map(command ->
                                        colorize(command.key(), BLUE) + " - " + command.description() + ": " + colorize(command.usage(), YELLOW)
                                )
                                .collect(Collectors.joining("\n"))
                )
                .build();
    }

}

