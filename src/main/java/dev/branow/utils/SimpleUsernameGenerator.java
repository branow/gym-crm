package dev.branow.utils;


import dev.branow.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SimpleUsernameGenerator implements UsernameGenerator {

    @Override
    public <T extends User> String generate(T user, Stream<T> users) {
        var lastUserIndex = users
                .filter(u -> u.getFirstName().equals(user.getFirstName()) && u.getLastName().equals(user.getLastName()))
                .map(u -> u.getUsername().replaceAll(generate(u, -1), ""))
                .map(num -> num.isEmpty() ? 0 : Integer.parseInt(num))
                .max(Integer::compareTo)
                .orElse(-1);
        return generate(user, lastUserIndex);
    }

    private <T extends User> String generate(T user, int index) {
        var indexStr = index == -1 ? "" : String.valueOf(index + 1);
        return user.getFirstName() + "." + user.getLastName() + indexStr;
    }
}
