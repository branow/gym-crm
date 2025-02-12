package dev.branow.cli;

import dev.branow.auth.Credentials;
import dev.branow.auth.CredentialsProvider;
import dev.branow.auth.SimpleCredentials;
import org.springframework.stereotype.Component;

import java.util.Scanner;

import static dev.branow.cli.Color.BLUE;
import static dev.branow.cli.Color.colorize;

@Component
public class CliCredentialsProvider implements CredentialsProvider {

    @Override
    public Credentials getCredentials() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(colorize("Username> ", BLUE));
        var username = scanner.nextLine();
        System.out.print(colorize("Password> ", BLUE));
        var password = scanner.nextLine();
        return new SimpleCredentials(username, password);
    }

}
