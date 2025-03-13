package dev.branow.services;

import dev.branow.annotations.Log;
import dev.branow.dtos.service.ChangePasswordDto;
import dev.branow.dtos.service.CredentialsDto;
import dev.branow.dtos.service.UpdateUserDto;
import dev.branow.log.Level;
import dev.branow.mappers.UserMapper;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    @Log(value = "getting user by username %0", level = Level.DEBUG)
    public User getByUsername(String username) {
        return repository.getReferenceByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .map(mapper::mapUserDetailsDto)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public String prepareUserForCreation(User user) {
        var username = generateUsername(user);
        var password = generatePassword();
        var encodedPassword = encoder.encode(password);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setIsActive(false);
        return password;
    }

    public void applyUserUpdates(User user, UpdateUserDto dto) {
        user.setIsActive(dto.getIsActive());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
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
