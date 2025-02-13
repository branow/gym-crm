package dev.branow.mappers;

import dev.branow.dtos.UserDto;
import dev.branow.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .build();
    }

}
