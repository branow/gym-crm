package dev.branow.services;

import dev.branow.DBTest;
import dev.branow.dtos.service.ChangePasswordDto;
import dev.branow.dtos.service.UpdateUserDto;
import dev.branow.mappers.UserMapper;
import dev.branow.model.User;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import jakarta.persistence.EntityManager;
import jakarta.validation.ValidationException;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({
        UserMapper.class,
        UserService.class
})
@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends DBTest {

    @MockitoBean
    private PasswordGenerator passwordGenerator;
    @MockitoBean
    private UsernameGenerator usernameGenerator;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager manager;
    @Autowired
    private UserMapper mapper;
    @Autowired
    private UserService service;

    @Test
    public void testGetByUsername_withPresentUser_returnUser() {
        var expectedUser = User.builder()
                .id(1L)
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .password("RM9AVLZpCK")
                .isActive(true)
                .build();
        var actualUser = service.getByUsername(expectedUser.getUsername());
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        assertEquals(expectedUser.getIsActive(), actualUser.getIsActive());
    }

    @Test
    public void testGetByUsername_withAbsentUser_throwException() {
        assertThrows(ObjectNotFoundException.class, () -> service.getByUsername("absentUsername"));
    }

    @Test
    public void testPrepareForCreation() {
        var user = User.builder()
                .firstName("Bob")
                .lastName("Doe")
                .build();
        var username = "John.Doe";
        var password = "password";

        when(usernameGenerator.generate(eq(user), any())).thenReturn(username);
        when(passwordGenerator.generate()).thenReturn(password);
        when(passwordEncoder.encode(password)).thenReturn(password);

        service.prepareUserForCreation(user);

        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertFalse(user.getIsActive());
    }

    @Test
    public void testApplyUserUpdates() {
        var user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password("RM9AVLZpCK")
                .isActive(true)
                .build();

        var dto = UpdateUserDto.builder()
                .firstName("John1")
                .lastName("Doe1")
                .isActive(false)
                .username("John.Doe2")
                .build();

        service.applyUserUpdates(user, dto);

        assertEquals(dto.getFirstName(), user.getFirstName());
        assertEquals(dto.getLastName(), user.getLastName());
        assertEquals(dto.getIsActive(), user.getIsActive());
        assertEquals("John.Doe", user.getUsername());
        assertEquals("RM9AVLZpCK", user.getPassword());
        assertEquals(1L, user.getId());
    }

    @Test
    public void testToggleActive() {
        var username = "John.Doe";
        var query = String.format("select u.isActive from %s u where u.username = '%s'", User.class.getName(), username);
        Supplier<Boolean> stateGetter = () -> manager.createQuery(query, Boolean.class).getSingleResult();

        var expectedState = !stateGetter.get();
        service.toggleActive(username);
        var actualState = stateGetter.get();
        assertEquals(expectedState, actualState);

        expectedState = !stateGetter.get();
        service.toggleActive(username);
        actualState = stateGetter.get();
        assertEquals(expectedState, actualState);
    }

    @Test
    public void changePassword_withInvalidOldPassword_throwException() {
        var password = new ChangePasswordDto("invalid password", "new password");
        assertThrows(ValidationException.class, () -> service.changePassword("John.Doe", password));
    }

    @Test
    public void changePassword_withValidOldPassword_changePassword() {
        var password = new ChangePasswordDto("RM9AVLZpCK", "new-password");
        service.changePassword("John.Doe", password);
        var actualPassword = manager.find(User.class, 1L).getPassword();
        assertEquals(password.getNewPassword(), actualPassword);
    }

}
