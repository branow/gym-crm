package dev.branow.controllers;

import dev.branow.dtos.request.LoginRequest;
import dev.branow.dtos.service.ChangePasswordDto;
import dev.branow.mappers.UserMapper;
import dev.branow.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {
        var token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        authenticationManager.authenticate(token);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable("username") String username,
            @RequestBody @Valid ChangePasswordDto dto
    ) {
        service.changePassword(username, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{username}/toggle")
    public ResponseEntity<?> toggleActivation(@PathVariable("username") String username) {
        service.toggleActive(username);
        return ResponseEntity.ok().build();
    }

}
