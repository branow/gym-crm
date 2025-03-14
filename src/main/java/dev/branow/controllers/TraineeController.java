package dev.branow.controllers;

import dev.branow.annotations.Authorize;
import dev.branow.dtos.request.CreateTraineeRequest;
import dev.branow.dtos.request.UpdateFavoriteTrainersRequest;
import dev.branow.dtos.request.UpdateTraineeRequest;
import dev.branow.dtos.response.TrainingResponse;
import dev.branow.dtos.service.ShortTrainerDto;
import dev.branow.dtos.response.CredentialsResponse;
import dev.branow.dtos.response.TraineeResponse;
import dev.branow.mappers.TraineeMapper;
import dev.branow.mappers.TrainingMapper;
import dev.branow.security.JwtService;
import dev.branow.security.authorization.UserAuthorizer;
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
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<CredentialsResponse> create(@RequestBody @Valid CreateTraineeRequest request) {
        var dto = mapper.mapCreateTraineeDto(request);
        var trainee = service.create(dto);
        var user = mapper.mapUserDetailsDto(trainee);
        var credentials = mapper.mapCredentialsResponse(trainee, jwtService.generate(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials);
    }

    @GetMapping("/{username}")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<TraineeResponse> get(@PathVariable("username") String username) {
        var trainee = mapper.mapTraineeResponse(service.getByUsername(username));
        return ResponseEntity.ok(trainee);
    }

    @PutMapping("/{username}")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<TraineeResponse> update(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateTraineeRequest request
    ) {
        var updateDto = mapper.mapUpdateTraineeDto(username, request);
        var trainee = service.update(updateDto);
        var response = mapper.mapTraineeResponse(trainee);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<?> delete(@PathVariable("username") String username) {
        service.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{username}/favorite-trainers")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<List<ShortTrainerDto>> updateFavouriteTrainers(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateFavoriteTrainersRequest request
    ) {
        var updateDto = mapper.mapUpdateFavouriteTrainersDto(username, request);
        var trainers = service.updateFavoriteTrainers(updateDto);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{username}/trainings")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<List<TrainingResponse>> getTrainings(
            @PathVariable("username") String username,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "trainer", required = false) String trainer,
            @RequestParam(value = "type", required = false) Long type
    ) {
        var filter = mapper.mapCriteriaTrainingTraineeDto(username, from, to, trainer, type);
        var trainings = trainingService.getAllByTraineeUsernameCriteria(filter);
        var response = trainings.stream().map(trainingMapper::mapTrainingResponse).toList();
        return ResponseEntity.ok(response);
    }

}
