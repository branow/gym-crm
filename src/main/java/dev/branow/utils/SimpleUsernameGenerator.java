package dev.branow.utils;

import dev.branow.model.User;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class SimpleUsernameGenerator implements UsernameGenerator {

    @Override
    public <T extends User> String generate(T user, Stream<T> users) {
        var lastUserIndex = users
                .filter((u) -> hasSameName(u, user))
                .map(this::extractIndexFromUsername)
                .max(Integer::compareTo)
                .orElse(-1);
        return formatUsername(user, lastUserIndex);
    }

    private boolean hasSameName(User u1, User u2) {
        return !Objects.equals(u1.getId(), u2.getId())
                && u1.getFirstName().equals(u2.getFirstName())
                && u1.getLastName().equals(u2.getLastName());
    }

    private int extractIndexFromUsername(User user) {
        String baseUsername = formatUsername(user, -1);
        String suffix = user.getUsername().replace(baseUsername, "");
        return suffix.isEmpty() ? 0 : Integer.parseInt(suffix);
    }

    private String formatUsername(User user, int index) {
        var indexStr = index == -1 ? "" : String.valueOf(index + 1);
        return user.getFirstName() + "." + user.getLastName() + indexStr;
    }

}
