package dev.branow.cli;

import dev.branow.dtos.UserDto;
import dev.branow.model.User;
import dev.branow.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(UserCommands.class)
public class UserCommandsTest {

    @MockitoBean
    private UserService service;

    @Test
    public void testToggleActivation(
            @Qualifier("toggleUserActivation")
            @Autowired Command command) {
        when(service.toggleActive(any())).thenReturn(new UserDto());
        command.execute("toggle", "username");
        verify(service, times(1)).toggleActive(any());
    }

    @Test
    public void testChangePassword(
            @Qualifier("changeUserPassword")
            @Autowired Command command) {
        when(service.changePassword(any(), any())).thenReturn(new UserDto());
        command.execute("cpass", "username", "oldpass", "newpass", "confirmpass");
        verify(service, times(1)).changePassword(any(), any());
    }

}
