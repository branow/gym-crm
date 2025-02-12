package dev.branow.services;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.annotations.Log;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.CreateTraineeDto;
import dev.branow.dtos.UpdateTraineeDto;
import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.mappers.TraineeMapper;
import dev.branow.model.Trainee;
import dev.branow.repositories.TraineeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TraineeService {

    private final TraineeRepository repository;
    private final TraineeMapper mapper;
    private final UserService userService;

    @Authenticate
    @Authorize(UserAuthorizer.Id.class)
    @Log("getting trainee by id %0")
    public Trainee getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class, id));
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("getting trainee by username %0")
    public Trainee getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class, username));
    }

    @Log("creating trainee with %0")
    public Trainee create(@Valid CreateTraineeDto dto) {
        var trainee = mapper.toTrainee(dto);
        userService.prepareUserForCreation(trainee);
        return repository.save(trainee);
    }

    @Authenticate
    @Authorize(UserAuthorizer.UpdateTraineeDto.class)
    @Log("updating trainee with %0")
    public Trainee update(@Valid UpdateTraineeDto dto) {
        var trainee = getById(dto.getId());
        userService.applyUserUpdates(trainee, dto);
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());
        return repository.save(trainee);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @Log("deleting trainee by username %0")
    public void deleteByUsername(String username) {
        repository.deleteByUsername(username);
    }

}
