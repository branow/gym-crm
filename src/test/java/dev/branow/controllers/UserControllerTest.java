package dev.branow.controllers;

import dev.branow.dtos.request.LoginRequest;
import dev.branow.dtos.response.JwtResponse;
import dev.branow.dtos.service.ChangePasswordDto;
import dev.branow.mappers.UserMapper;
import dev.branow.security.JwtRevocationService;
import dev.branow.security.JwtService;
import dev.branow.security.LoginAttemptService;
import dev.branow.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static dev.branow.controllers.JsonMapper.toJson;
import static dev.branow.controllers.RestUtils.rest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAspectJAutoProxy
@SpringJUnitConfig({
        UserController.class,
        UserMapper.class,
        CustomExceptionHandler.class,
})
public class UserControllerTest {

    @MockitoBean
    private UserService service;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private JwtRevocationService jwtRevocationService;
    @MockitoBean
    private LoginAttemptService loginAttemptService;

    @Autowired
    private UserController controller;
    @Autowired
    private UserMapper mapper;
    @Autowired
    private CustomExceptionHandler exceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    public void testLogin_tooManyAttempts_return401() throws Exception {
        var loginRequest = new LoginRequest("username", "password");
        when(loginAttemptService.isBlocked(any())).thenReturn(true);
        var request = rest(post("/users/login")).content(toJson(loginRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_authenticationException_return401() throws Exception {
        var loginRequest = new LoginRequest("username", "password");
        var token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        when(loginAttemptService.isBlocked(any())).thenReturn(false);
        when(authenticationManager.authenticate(token)).thenThrow(new BadCredentialsException(""));

        var request = rest(post("/users/login")).content(toJson(loginRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(loginAttemptService, times(1)).recordFailure(any());
    }

    @Test
    public void testLogin() throws Exception {
        var loginRequest = new LoginRequest("username", "password");
        var token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        var jwt = "jwt";
        var authentication = mock(Authentication.class);
        var jwtResponse = new JwtResponse(jwt);

        when(loginAttemptService.isBlocked(any())).thenReturn(false);
        when(authenticationManager.authenticate(token)).thenReturn(authentication);
        when(jwtService.generate(authentication)).thenReturn(jwt);

        var request = rest(post("/users/login")).content(toJson(loginRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(jwtResponse)));

        verify(loginAttemptService, times(1)).recordSuccess(any());
    }

    @Test
    public void testChangePassword_successful() throws Exception {
        var password = ChangePasswordDto.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();
        var request = rest(put("/users/John.Doe/password"))
                .content(toJson(password));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).changePassword("John.Doe", password);
    }

    @ParameterizedTest
    @MethodSource("provideTestChangePassword_invalidPassword_return422")
    public void testChangePassword_invalidPassword_return422(ChangePasswordDto password) throws Exception {
        var request = rest(put("/users/John.Doe/password"))
                .content(toJson(password));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        verify(service, never()).changePassword("John.Doe", password);
    }

    private static Stream<Arguments> provideTestChangePassword_invalidPassword_return422() {
        return Stream.of(
                Arguments.of(ChangePasswordDto.builder().oldPassword(null).newPassword(null).build()),
                Arguments.of(ChangePasswordDto.builder().oldPassword("").newPassword(null).build()),
                Arguments.of(ChangePasswordDto.builder().oldPassword(null).newPassword("").build()),
                Arguments.of(ChangePasswordDto.builder().oldPassword("oldPassword").newPassword("1".repeat(7)).build()),
                Arguments.of(ChangePasswordDto.builder().oldPassword("oldPassword").newPassword("1".repeat(21)).build())
        );
    }

    @Test
    public void testToggleActivation() throws Exception {
        var request = rest(patch("/users/John.Doe/toggle"));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).toggleActive("John.Doe");
    }

}
