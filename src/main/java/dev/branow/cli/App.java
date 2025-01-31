package dev.branow.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class App {

    public static final String DASH = "-";

    private final List<Command> commands;

    public void start() {
        Scanner scanner = new Scanner(System.in);
        var line = "";
        System.out.println(help().execute(new String[]{}));
        System.out.print(">");
        while ((line = scanner.nextLine()) != null) {

            line = line.trim();
            var args = Arrays.stream(line.split(" "))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            if (args.length == 0) {
                System.err.println("No command");
            }

            var key = args[0];
            if (key.equals("exit")) {
                break;
            }

            Command command = null;
            if (key.equals("help")) {
                command = help();
            }

            for (var cmd : commands) {
                if (key.equals(cmd.key())) {
                    command = cmd;
                }
            }

            if (command == null) {
                System.err.println("Unknown command: " + key);
                command = help();
            }

            String response;
            try {
                response = command.execute(args);
            } catch (Exception e) {
                log.error(e.getMessage());
                continue;
            }

            if (response.equals("@")) {
                System.out.println("Usage: " + command.usage());
            } else {
                System.out.println(response);
            }
            System.out.print(">");
        }
    }

    public Command help() {
        return Command.builder()
                .key("help")
                .usage("help")
                .executor(_ ->
                    commands.stream()
                        .map(Command::usage)
                        .collect(Collectors.joining("\n"))
                )
                .build();
    }

}

