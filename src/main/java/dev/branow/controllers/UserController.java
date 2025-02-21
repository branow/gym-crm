package dev.branow.controllers;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.request.LoginRequest;
import dev.branow.dtos.service.ChangePasswordDto;
import dev.branow.mappers.UserMapper;
import dev.branow.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {
        var credentials = mapper.toCredentialsDto(loginRequest);
        service.matchCredentials(credentials);
        return ResponseEntity.ok().build();
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @PutMapping("/{username}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable("username") String username,
            @RequestBody @Valid ChangePasswordDto dto
    ) {
        service.changePassword(username, dto);
        return ResponseEntity.ok().build();
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @PatchMapping("/{username}/toggle")
    public ResponseEntity<?> toggleActivation(@PathVariable("username") String username) {
        service.toggleActive(username);
        return ResponseEntity.ok().build();
    }

}
