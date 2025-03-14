package dev.branow.controllers;

import dev.branow.dtos.request.LoginRequest;
import dev.branow.dtos.response.JwtResponse;
import dev.branow.dtos.service.ChangePasswordDto;
import dev.branow.exceptions.LoginAttemptLimitExceededException;
import dev.branow.security.JwtRevocationService;
import dev.branow.security.JwtService;
import dev.branow.security.LoginAttemptService;
import dev.branow.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtRevocationService jwtRevocationService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            HttpServletRequest request,
            @RequestBody LoginRequest loginRequest
    ) {
        var ip = request.getRemoteAddr();
        if (loginAttemptService.isBlocked(ip))
            throw new LoginAttemptLimitExceededException(LoginAttemptService.LOCK_TIME);

        try {
            var token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            var authentication = authenticationManager.authenticate(token);
            loginAttemptService.recordSuccess(request.getRemoteAddr());
            var jwt = jwtService.generate(authentication);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (AuthenticationException e) {
            loginAttemptService.recordFailure(ip);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        jwtRevocationService.revoke(jwt.getTokenValue());
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
