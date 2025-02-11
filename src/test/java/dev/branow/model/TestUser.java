package dev.branow.model;

import dev.branow.MockDB;
import dev.branow.TestDBConfig;
import dev.branow.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static dev.branow.EntityManagerUtils.*;
import static dev.branow.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestDBConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestUser {

    @Autowired
    private EntityManager manager;

    @AfterEach
    public void cleanUp() {
        clean(manager);
    }

    @Test
    @Order(1)
    public void testPersistUser_withValidUser_persisted() {
        User user = nextUser();

        var expectedId = lastId(manager, User.class.getName(), "id") + 1;
        var expected = TestDataFactory.clone(user);
        expected.setId(expectedId);
        persist(manager, user);

        assertEquals(expectedId, user.getId());
        assertEquals(expected, user);
    }

    @Test
    public void testPersistUser_withAbsentFirstName_throwsException() {
        var user = nextUser();
        user.setFirstName(null);
        assertThrows(ConstraintViolationException.class, () -> persist(manager, user));
    }

    @Test
    public void testPersistUser_withTooLongFirstName_throwsException() {
        var validUser = nextUser();
        validUser.setFirstName("a".repeat(45));
        persist(manager, validUser);

        var invalidUser = nextUser();
        invalidUser.setFirstName("a".repeat(46));
        assertThrows(DataException.class, () -> persist(manager, invalidUser));
    }

    @Test
    public void testPersistUser_withAbsentLastName_throwsException() {
        var user = nextUser();
        user.setLastName(null);
        assertThrows(ConstraintViolationException.class, () -> persist(manager, user));
    }

    @Test
    public void testPersistUser_withTooLongLastName_throwsException() {
        var validUser = nextUser();
        validUser.setLastName("a".repeat(45));
        persist(manager, validUser);

        var invalidUser = nextUser();
        invalidUser.setLastName("a".repeat(46));
        assertThrows(DataException.class, () -> persist(manager, invalidUser));
    }

    @Test
    public void testPersistUser_withAbsentUsername_throwsException() {
        var user = nextUser();
        user.setUsername(null);
        assertThrows(ConstraintViolationException.class, () -> persist(manager, user));
    }

    @Test
    public void testPersistUser_withRepeatedUsername_throwsException() {
        var persistedUser = manager.find(User.class, 1L);
        var user = nextUser();
        user.setUsername(persistedUser.getUsername());
        assertThrows(ConstraintViolationException.class, () -> persist(manager, user));
    }

    @Test
    public void testPersistUser_withTooLongUsername_throwsException() {
        var validUser = nextUser();
        validUser.setUsername("a".repeat(100));
        persist(manager, validUser);

        var invalidUser = nextUser();
        invalidUser.setUsername("a".repeat(101));
        assertThrows(DataException.class, () -> persist(manager, invalidUser));
    }

    @Test
    public void testPersistUser_withAbsentPassword_throwsException() {
        var user = nextUser();
        user.setPassword(null);
        assertThrows(ConstraintViolationException.class, () -> persist(manager, user));
    }

    @Test
    public void testPersistUser_withTooLongPassword_throwsException() {
        var validUser = nextUser();
        validUser.setPassword("a".repeat(60));
        persist(manager, validUser);

        var invalidUser = nextUser();
        invalidUser.setPassword("a".repeat(61));
        assertThrows(DataException.class, () -> persist(manager, invalidUser));
    }

    @Test
    public void testPersistUser_withAbsentIsActive_throwsException() {
        var user = nextUser();
        user.setIsActive(null);
        assertThrows(ConstraintViolationException.class, () -> persist(manager, user));
    }

    @Test
    public void testRemoveUser_asTrainee_removeTraineeAndUser() {
        var user = (User) nextTrainee(null);
        persist(manager, user);
        var id = user.getId();
        remove(manager, user);

        assertNull(manager.find(User.class, id));
        assertNull(manager.find(Trainee.class, id));
    }

    @Test
    public void testRemoveUser_asTrainer_removeTrainerAndUser() {
        var spec = manager.find(TrainingType.class, 1L);
        var user = (User) nextTrainer(spec, null);
        persist(manager, user);
        var id = user.getId();

        remove(manager, user);
        assertNull(manager.find(User.class, id));
        assertNull(manager.find(Trainer.class, id));
    }

}
