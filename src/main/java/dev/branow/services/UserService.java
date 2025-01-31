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

@RequiredArgsConstructor
public class UserService<ID, T extends User> {

    protected final Repository<ID, T> repository;
    private final List<? extends Repository<ID, ? extends User>> repositories;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    public T getById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
    }

    public List<T> getAll() {
        return repository.findAll().collect(Collectors.toList());
    }

    public T create(T user) {
        user.setUsername(usernameGenerator.generate(user, getAllUsers()));
        user.setPassword(passwordGenerator.generate());
        return repository.create(user);
    }

    public T update(ID id, T user) {
        var oldUser = this.getById(id);
        if (!oldUser.getFirstName().equals(user.getFirstName()) ||
                !oldUser.getLastName().equals(user.getLastName())) {
            user.setUsername(usernameGenerator.generate(user, getAllUsers()));
        }
        return repository.update(user);
    }

    public void deleteById(ID id) {
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
