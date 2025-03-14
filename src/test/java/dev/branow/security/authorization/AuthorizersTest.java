package dev.branow.security.authorization;

import dev.branow.dtos.request.CreateTrainingRequest;
import dev.branow.dtos.service.UpdateTraineeDto;
import dev.branow.dtos.service.UpdateTrainerDto;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Test
    public void testAuthorizeByUsername(@Autowired UserAuthorizer.Username authorizer) {
        var resource = "username";
        when(userRepository.getReferenceByUsername(resource)).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    @Test
    public void testAuthorizeByCreateTrainingRequest_traineeUsername(@Autowired UserAuthorizer.CreateTrainingRequest authorizer) {
        var resource = CreateTrainingRequest.builder()
                .trainee("username")
                .trainer("trainer")
                .build();
        when(userRepository.getReferenceByUsername(resource.getTrainee())).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    @Test
    public void testAuthorizeByCreateTrainingRequest_trainerUsername(@Autowired UserAuthorizer.CreateTrainingRequest authorizer) {
        var resource = CreateTrainingRequest.builder()
                .trainee("trainee")
                .trainer("username")
                .build();
        when(userRepository.getReferenceByUsername(resource.getTrainer())).thenReturn(user);
        testAuthorize(authorizer, resource);
    }

    private<T> void testAuthorize(Authorizer<T> authorizer, T resource) {
        var authentication = new UsernamePasswordAuthenticationToken("username", "password");
        when(user.getUsername()).thenReturn(authentication.getName() + "0");
        assertThrows(AccessDeniedException.class, () -> authorizer.authorize(resource, authentication));
        when(user.getUsername()).thenReturn(authentication.getName());
        assertDoesNotThrow(() -> authorizer.authorize(resource, authentication));
    }

}
