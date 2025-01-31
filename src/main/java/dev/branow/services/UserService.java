package dev.branow.services;

import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.model.User;
import dev.branow.repositories.Repository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserService<ID, T extends User> {

    protected final Repository<ID, T> repository;
    private final List<? extends Repository<ID, ? extends User>> repositories;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    public T getById(ID id) {
        log.debug("Getting user by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found by id: {}", id);
                    return new EntityNotFoundException(User.class, id);
                });
    }

    public List<T> getAll() {
        return repository.findAll().collect(Collectors.toList());
    }

    public T create(T user) {
        log.info("Creating new user: {}", user);
        user.setUsername(usernameGenerator.generate(user, getAllUsers()));
        user.setPassword(passwordGenerator.generate());
        var newUser = repository.create(user);
        log.info("User created successfully: {}", newUser);
        return newUser;
    }

    public T update(ID id, T user) {
        log.info("Updating user: {}", user);
        var oldUser = this.getById(id);

        if (!oldUser.getFirstName().equals(user.getFirstName()) ||
                !oldUser.getLastName().equals(user.getLastName())) {
            log.debug("User {}: regenerating username", id);
            user.setUsername(usernameGenerator.generate(user, getAllUsers()));
        }

        var updatedUser = repository.update(user);
        log.info("User {} updated successfully", id);
        return updatedUser;
    }

    public void deleteById(ID id) {
        log.info("Deleting user with id: {}", id);
        repository.deleteById(id);
    }

    private Stream<User> getAllUsers() {
        return repositories.stream()
                .map(Repository::findAll)
                .reduce(Stream::concat)
                .orElse(Stream.empty())
                .map(user -> (User) user);
    }

}
