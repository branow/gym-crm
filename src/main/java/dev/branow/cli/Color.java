package dev.branow.cli;

public class Color {
    // ANSI escape codes for text colors
    public static final String
            RESET = "\u001B[0m",
            RED = "\u001B[31m",
            GREEN = "\u001B[32m",
            YELLOW = "\u001B[33m",
            BLUE = "\u001B[34m",
            PURPLE = "\u001B[35m",
            CYAN = "\u001B[36m",
            WHITE = "\u001B[37m";

    public static String colorize(String text, String color) {
        return color + text + RESET;
    }
}
