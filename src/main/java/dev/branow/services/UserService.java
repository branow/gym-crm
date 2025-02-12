package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.ChangePasswordDto;
import dev.branow.dtos.UpdateUserDto;
import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.log.Level;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    @Log(value = "getting user by id %0", level = Level.DEBUG)
    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
    }

    @Log(value = "getting user by username %0", level = Level.DEBUG)
    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class, username));
    }

    public void prepareUserForCreation(User user) {
        var username = generateUsername(user);
        var password = generatePassword();
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(false);
    }

    public void applyUserUpdates(User user, UpdateUserDto dto) {
        if (hasSameName(user, dto)) return;

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        var username = generateUsername(user);
        user.setUsername(username);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("toggling user activation for %0")
    public User toggleActive(String username) {
        var user = getByUsername(username);
        user.setIsActive(!user.getIsActive());
        return repository.save(user);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("changing password for %0")
    public User changePassword(String username, @Valid ChangePasswordDto dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        var user = getByUsername(username);
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        user.setPassword(dto.getNewPassword());
        return repository.save(user);
    }

    private boolean hasSameName(User oldUser, UpdateUserDto newUser) {
        return oldUser.getFirstName().equals(newUser.getFirstName()) &&
                oldUser.getLastName().equals(newUser.getLastName());
    }

    private String generateUsername(User user) {
        var userStream = repository.findAll().stream();
        return usernameGenerator.generate(user, userStream);
    }

    private String generatePassword() {
        return passwordGenerator.generate();
    }

}
