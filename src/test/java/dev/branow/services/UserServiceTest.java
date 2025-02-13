package dev.branow.services;

import dev.branow.config.ValidationConfig;
import dev.branow.dtos.ChangePasswordDto;
import dev.branow.dtos.UpdateUserDto;
import dev.branow.mappers.UserMapper;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.stream.Stream;

import static dev.branow.services.ValidationTest.testValidation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({ ValidationConfig.class, UserService.class, UserMapper.class })
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @MockitoBean
    private UserRepository repository;
    @MockitoBean
    private PasswordGenerator passwordGenerator;
    @MockitoBean
    private UsernameGenerator usernameGenerator;

    @Autowired
    private UserMapper mapper;
    @Autowired
    private UserService service;

    @Test
    public void testGetById_withPresentUser_returnUser() {
        var id = 123L;
        var user = User.builder().id(id).build();
        when(repository.getReferenceById(id)).thenReturn(user);
        var actual = service.getById(id);
        assertEquals(user, actual);
    }

    @Test
    public void testGetByUsername_withPresentUser_returnUser() {
        var username = "Bob.Doe";
        var user = User.builder().username(username).build();
        when(repository.getReferenceByUsername(username)).thenReturn(user);
        var actual = service.getByUsername(username);
        assertEquals(user, actual);
    }

    @Test
    public void testPrepareForCreation() {
        var user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        var username = "John.Doe";
        var password = "password";
        var expected = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(username)
                .password("password")
                .isActive(false)
                .build();

        when(usernameGenerator.generate(eq(user), any())).thenReturn(username);
        when(passwordGenerator.generate()).thenReturn(password);

        service.prepareUserForCreation(user);
        assertEquals(expected, user);
    }

    @Test
    public void testApplyUserUpdates() {
        var user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .build();
        var updateDto = UpdateUserDto.builder()
                .firstName("Bob")
                .lastName("Smith")
                .build();
        var updatedUser = User.builder()
                .firstName(updateDto.getFirstName())
                .lastName(updateDto.getLastName())
                .username(user.getUsername())
                .build();
        var username = "Bob.Smith";
        var expected = User.builder()
                .firstName(updateDto.getFirstName())
                .lastName(updateDto.getLastName())
                .username(username)
                .build();

        when(usernameGenerator.generate(eq(updatedUser), any())).thenReturn(username);

        service.applyUserUpdates(user, updateDto);
        assertEquals(expected, user);
    }

    @Test
    public void testToggleActive() {
        var expected = User.builder()
                .username("username")
                .isActive(false)
                .build();
        var foundUser = User.builder()
                .username(expected.getUsername())
                .isActive(true)
                .build();

        when(repository.getReferenceByUsername(expected.getUsername())).thenReturn(foundUser);
        when(repository.save(expected)).thenReturn(expected);

        var actual = service.toggleActive(expected.getUsername());
        assertEquals(mapper.toUserDto(expected), actual);
    }

    @ParameterizedTest
    @MethodSource("provideTestChangePassword_invalidPasswordDto_throwException")
    public void testChangePassword_ChangePasswordDtoValidation(ChangePasswordDto dto, boolean isValid) {
        var username = "username";
        var user = User.builder().password(dto.getOldPassword()).build();
        when(repository.getReferenceByUsername(username)).thenReturn(user);
        testValidation(isValid, () -> service.changePassword(username, dto));
    }

    private static Stream<Arguments> provideTestChangePassword_invalidPasswordDto_throwException() {
        return Stream.of(
                Arguments.of(ChangePasswordDto.builder().oldPassword(null).newPassword("password").confirmPassword("password").build(), false),
                Arguments.of(ChangePasswordDto.builder().oldPassword("").newPassword("password").confirmPassword("password").build(), true),
                Arguments.of(ChangePasswordDto.builder().oldPassword("password").newPassword(null).confirmPassword("password").build(), false),
                Arguments.of(ChangePasswordDto.builder().oldPassword("password").newPassword("password").confirmPassword(null).build(), false),
                Arguments.of(ChangePasswordDto.builder().oldPassword("password").newPassword("1234567").confirmPassword("password").build(), false),
                Arguments.of(ChangePasswordDto.builder().oldPassword("password").newPassword("password").confirmPassword("1234567").build(), false)
        );
    }


    @Test
    public void changePassword_withNotEqualPasswords_throwException() {
        var passwords = ChangePasswordDto.builder()
                .oldPassword("password0")
                .newPassword("password1")
                .confirmPassword("password2")
                .build();
        assertThrows(IllegalArgumentException.class, () -> service.changePassword("", passwords));
    }

    @Test
    public void changePassword_withInvalidOldPassword_throwException() {
        var username = "John.Smith";
        var user = User.builder().username(username).password("password").build();
        var passwords = ChangePasswordDto.builder()
                .oldPassword("password1")
                .newPassword("password2")
                .confirmPassword("password2")
                .build();

        when(repository.getReferenceByUsername(username)).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> service.changePassword(username, passwords));
    }

    @Test
    public void changePassword_withEqualPasswords_changePassword() {
        var expected = User.builder()
                .username("Bob")
                .password("new password")
                .build();
        var oldUser = User.builder()
                .username(expected.getUsername())
                .password("old password")
                .build();
        var passwords = ChangePasswordDto.builder()
                .oldPassword(oldUser.getPassword())
                .newPassword(expected.getPassword())
                .confirmPassword(expected.getPassword())
                .build();

        when(repository.getReferenceByUsername(expected.getUsername())).thenReturn(oldUser);
        when(repository.save(expected)).thenReturn(expected);

        var actual = service.changePassword(expected.getUsername(), passwords);
        assertEquals(mapper.toUserDto(expected), actual);
    }

}
