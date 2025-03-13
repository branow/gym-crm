package dev.branow.mappers;

import dev.branow.dtos.request.LoginRequest;
import dev.branow.dtos.service.CredentialsDto;
import dev.branow.dtos.service.UserDetailsDto;
import dev.branow.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDetailsDto mapUserDetailsDto(User user) {
        return UserDetailsDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public CredentialsDto mapCredentialsDto(LoginRequest request) {
        return new CredentialsDto(request.getUsername(), request.getPassword());
    }

}
