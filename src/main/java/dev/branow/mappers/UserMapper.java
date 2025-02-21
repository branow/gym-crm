package dev.branow.mappers;

import dev.branow.dtos.request.LoginRequest;
import dev.branow.dtos.service.CredentialsDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public CredentialsDto toCredentialsDto(LoginRequest request) {
        return new CredentialsDto(request.getUsername(), request.getPassword());
    }

}
