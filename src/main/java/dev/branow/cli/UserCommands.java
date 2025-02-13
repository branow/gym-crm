package dev.branow.cli;

import dev.branow.dtos.ChangePasswordDto;
import dev.branow.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommands {

    private final UserService service;

    @Bean("toggleUserActivation")
    public Command toggleActivation() {
        return Command.builder()
                .key("toggle")
                .description("Toggles the Activation of User")
                .usage("toggle <username!>")
                .executor((args) -> {
                    var parser = ArgsParser.of(args);
                    var username = parser.parse(1, String.class).get();
                    return service.toggleActive(username).toString();
                })
                .build();
    }

    @Bean("changeUserPassword")
    public Command changePassword() {
        return Command.builder()
                .key("cpass")
                .description("Changes the Password")
                .usage("cpass <username!> <oldPassword!> <newPassword!> <confirmPassword!>")
                .executor((args) -> {
                    var parser = ArgsParser.of(args);
                    var username = parser.parse(1, String.class).get();
                    var dto = new ChangePasswordDto();
                    dto.setOldPassword(parser.parse(2, String.class).get());
                    dto.setNewPassword(parser.parse(3, String.class).get());
                    dto.setConfirmPassword(parser.parse(4, String.class).get());
                    return service.changePassword(username, dto).toString();
                })
                .build();
    }

}
