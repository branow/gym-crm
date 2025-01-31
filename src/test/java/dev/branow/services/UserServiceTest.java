package dev.branow.services;

import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.model.User;
import dev.branow.repositories.Repository;
import dev.branow.utils.PasswordGenerator;
import dev.branow.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private TestRepository repository;
    @Mock
    private List<TestRepository> repositories;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UsernameGenerator usernameGenerator;

    private UserService<Long, User> service;

    @BeforeEach
    void setUp() {
        service = new UserService<>(repository, repositories, passwordGenerator, usernameGenerator);
    }

    @Test
    public void testGetById_isPresent_returnUser() {
        var expected = new User();
        var id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        var actual = service.getById(id);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetById_isAbsent_throwException() {
        var id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(id));
    }

    @Test
    public void testGetAll() {
        var expected = List.of(
                User.builder().username("John").build(),
                User.builder().username("Bill").build()
        );
        when(repository.findAll()).thenReturn(expected.stream());
        var actual = service.getAll();
        assertEquals(expected, actual);
    }

    @Test
    public void testCreate() {
        var expected = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("generated-password")
                .build();
        var user = User.builder()
                .firstName(expected.getFirstName())
                .lastName(expected.getLastName())
                .build();
        when(repositories.stream()).thenReturn(Stream.of());
        when(usernameGenerator.generate(eq(user), any())).thenReturn(expected.getUsername());
        when(passwordGenerator.generate()).thenReturn(expected.getPassword());
        when(repository.create(expected)).thenReturn(expected);
        var actual = service.create(user);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdate_oldFirstNameAndLastName_oldUsername() {
        var id = 5123L;
        var user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("old password")
                .isActive(false)
                .build();
        var expected = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password("new password")
                .isActive(true)
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(repository.update(expected)).thenReturn(expected);
        var actual = service.update(id, expected);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdate_newFirstNameOrLastName_newUsername() {
        var id = 5123L;
        var user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .build();
        var expected = User.builder()
                .firstName("Bob")
                .lastName("Jackson")
                .username("Bob.Jackson")
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(usernameGenerator.generate(eq(expected), any())).thenReturn(expected.getUsername());
        when(repository.update(expected)).thenReturn(expected);
        var actual = service.update(id, expected);
        assertEquals(expected, actual);
    }

    @Test
    public void testDelete() {
        var id = 5123L;
        service.deleteById(id);
        verify(repository, times(1)).deleteById(id);
    }

    interface TestRepository extends Repository<Long, User> {}

}
