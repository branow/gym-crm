package dev.branow.controllers;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.request.CreateTraineeRequest;
import dev.branow.dtos.request.UpdateFavoriteTrainersRequest;
import dev.branow.dtos.request.UpdateTraineeRequest;
import dev.branow.dtos.response.TrainingResponse;
import dev.branow.dtos.service.ShortTrainerDto;
import dev.branow.dtos.response.CredentialsResponse;
import dev.branow.dtos.response.TraineeResponse;
import dev.branow.mappers.TraineeMapper;
import dev.branow.mappers.TrainingMapper;
import dev.branow.services.TraineeService;
import dev.branow.services.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService service;
    private final TraineeMapper mapper;
    private final TrainingService trainingService;
    private final TrainingMapper trainingMapper;

    @PostMapping
    public ResponseEntity<CredentialsResponse> create(@RequestBody @Valid CreateTraineeRequest request) {
        var dto = mapper.toCreateTraineeDto(request);
        var trainee = service.create(dto);
        var credentials = mapper.toCredentialsResponse(trainee);
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @GetMapping("/{username}")
    public ResponseEntity<TraineeResponse> get(@PathVariable("username") String username) {
        var trainee = mapper.toTraineeResponse(service.getByUsername(username));
        return ResponseEntity.ok(trainee);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @PutMapping("/{username}")
    public ResponseEntity<TraineeResponse> update(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateTraineeRequest request
    ) {
        var updateDto = mapper.toUpdateTraineeDto(username, request);
        var trainee = service.update(updateDto);
        var response = mapper.toTraineeResponse(trainee);
        return ResponseEntity.ok(response);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @DeleteMapping("/{username}")
    public ResponseEntity<?> delete(@PathVariable("username") String username) {
        service.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @PutMapping("/{username}/favorite-trainers")
    public ResponseEntity<List<ShortTrainerDto>> updateFavouriteTrainers(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateFavoriteTrainersRequest request
    ) {
        var updateDto = mapper.toUpdateFavouriteTrainersDto(username, request);
        var trainers = service.updateFavoriteTrainers(updateDto);
        return ResponseEntity.ok(trainers);
    }

    @Authenticate
    @Authorize(UserAuthorizer.Username.class)
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTrainings(
            @PathVariable("username") String username,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "trainer", required = false) String trainer,
            @RequestParam(value = "type", required = false) Long type
    ) {
        var filter = mapper.toCriteriaTrainingTraineeDto(username, from, to, trainer, type);
        var trainings = trainingService.getAllByTraineeUsernameCriteria(filter);
        var response = trainings.stream().map(trainingMapper::toTrainingResponse).toList();
        return ResponseEntity.ok(response);
    }

}
