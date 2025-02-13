package dev.branow.auth;

import dev.branow.auth.authorizers.Authorizers;
import dev.branow.auth.authorizers.SimpleUserAuthorizer;
import dev.branow.auth.authorizers.TrainingAuthorizer;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.CriteriaTrainingTraineeDto;
import dev.branow.dtos.CriteriaTrainingTrainerDto;
import dev.branow.dtos.UpdateTraineeDto;
import dev.branow.dtos.UpdateTrainerDto;
import dev.branow.exceptions.AccessDeniedException;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ Authorizers.class, SimpleUserAuthorizer.class})
public class AuthorizersTest {

    @MockitoBean
    private UserRepository userRepository;
    @Mock
    private User user;
    @Mock
    private Credentials credentials;

    @Autowired
    private UserAuthorizer.Username userAuthorizer;

    @Test
    public void testAuthorizeByUsername(@Autowired  UserAuthorizer.Username authorizer) {
        var resource = "username";
        when(userRepository.getReferenceByUsername(resource)).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    @Test
    public void testAuthorizeById(@Autowired  UserAuthorizer.Id authorizer) {
        var resource = 12L;
        when(userRepository.getReferenceById(resource)).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    @Test
    public void testAuthorizeByUpdateTraineeDto(@Autowired  UserAuthorizer.UpdateTraineeDto authorizer) {
        var resource = new UpdateTraineeDto();
        resource.setId(123L);
        when(userRepository.getReferenceById(resource.getId())).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    @Test
    public void testAuthorizeByUpdateTrainerDto(@Autowired  UserAuthorizer.UpdateTrainerDto authorizer) {
        var resource = new UpdateTrainerDto();
        resource.setId(123L);
        when(userRepository.getReferenceById(resource.getId())).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    @Test
    public void testAuthorizeByCriteriaTraineeDto(@Autowired TrainingAuthorizer.CriteriaTraineeDto authorizer) {
        var resource = CriteriaTrainingTraineeDto.builder().traineeUsername("username").build();
        when(userRepository.getReferenceByUsername(resource.getTraineeUsername())).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    @Test
    public void testAuthorizeByCriteriaTrainerDto(@Autowired TrainingAuthorizer.CriteriaTrainerDto authorizer) {
        var resource = CriteriaTrainingTrainerDto.builder().trainerUsername("username").build();
        when(userRepository.getByUsername(resource.getTraineeUsername())).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    private<T> void testAuthorize(Authorizer<T> authorizer, T resource) {
        var credentials = new SimpleCredentials("username", "password");
        when(user.getUsername()).thenReturn(credentials.getUsername() + "0");
        assertThrows(AccessDeniedException.class, () -> authorizer.authorize(resource, credentials));
        when(user.getUsername()).thenReturn(credentials.getUsername());
        assertDoesNotThrow(() -> authorizer.authorize(resource, credentials));
    }

}
