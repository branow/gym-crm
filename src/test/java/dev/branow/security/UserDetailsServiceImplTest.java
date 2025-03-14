package dev.branow.security;

import dev.branow.mappers.UserMapper;
import dev.branow.model.User;
import dev.branow.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({ UserDetailsServiceImpl.class, UserMapper.class })
public class UserDetailsServiceImplTest {

    @MockitoBean
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;
    @Autowired
    private UserDetailsService service;

    @Test
    public void testLoadUserByUsername_presentUser_returnUser() {
        var username = "username";
        var user = User.builder().build();
        var details = mapper.mapUserDetailsDto(user);
        when(repository.findByUsername(username)).thenReturn(Optional.of(user));
        var actualDetails = service.loadUserByUsername(username);
        assertEquals(details, actualDetails);
    }

    @Test
    public void testLoadUserByUsername_absentUser_throwException() {
        var username = "username";
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("admin"));
    }

}
