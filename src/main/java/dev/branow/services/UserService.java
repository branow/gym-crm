package dev.branow.services;

import dev.branow.annotations.Log;
import dev.branow.dtos.service.ChangePasswordDto;
import dev.branow.dtos.service.CredentialsDto;
import dev.branow.dtos.service.UpdateUserDto;
import dev.branow.log.Level;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

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
        user.setIsActive(dto.getIsActive());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
    }

    @Transactional
    @Log("matching user credentials")
    public void matchCredentials(CredentialsDto credentials) {
        repository.findByUsername(credentials.getUsername())
                .filter(user -> user.getPassword().equals(credentials.getPassword()))
                .orElseThrow(IllegalStateException::new); // TODO CORRECT EXCEPTION
    }

    @Transactional
    @Log("toggling user activation for %0")
    public void toggleActive(String username) {
        var user = getByUsername(username);
        user.setIsActive(!user.getIsActive());
    }

    @Transactional
    @Log("changing password for %0")
    public void changePassword(String username, ChangePasswordDto dto) {
        var user = getByUsername(username);
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new ValidationException("Password do not match");
        }
        user.setPassword(dto.getNewPassword());
    }

    private String generateUsername(User user) {
        var userStream = repository.findAll().stream();
        return usernameGenerator.generate(user, userStream);
    }

    private String generatePassword() {
        return passwordGenerator.generate();
    }

}
