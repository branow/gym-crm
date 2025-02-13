package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.ChangePasswordDto;
import dev.branow.dtos.UpdateUserDto;
import dev.branow.dtos.UserDto;
import dev.branow.log.Level;
import dev.branow.mappers.UserMapper;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import jakarta.transaction.Transactional;
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
    private final UserMapper mapper;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    @Log(value = "getting user by id %0", level = Level.DEBUG)
    public User getById(Long id) {
        return repository.getReferenceById(id);
    }

    @Log(value = "getting user by username %0", level = Level.DEBUG)
    public User getByUsername(String username) {
        return repository.getReferenceByUsername(username);
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

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("toggling user activation for %0")
    public UserDto toggleActive(String username) {
        var user = getByUsername(username);
        user.setIsActive(!user.getIsActive());
        return mapper.toUserDto(user);
    }

    @Transactional
    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("changing password for %0")
    public UserDto changePassword(String username, @Valid ChangePasswordDto dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        var user = repository.getReferenceByUsername(username);
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        user.setPassword(dto.getNewPassword());
        return mapper.toUserDto(user);
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
