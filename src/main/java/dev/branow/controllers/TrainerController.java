package dev.branow.controllers;

import dev.branow.annotations.Authorize;
import dev.branow.dtos.request.CreateTrainerRequest;
import dev.branow.dtos.request.UpdateTrainerRequest;
import dev.branow.dtos.response.CredentialsResponse;
import dev.branow.dtos.response.TrainerResponse;
import dev.branow.dtos.response.TrainingResponse;
import dev.branow.dtos.service.ShortTrainerDto;
import dev.branow.mappers.TrainerMapper;
import dev.branow.mappers.TrainingMapper;
import dev.branow.security.JwtService;
import dev.branow.security.authorization.UserAuthorizer;
import dev.branow.services.TrainerService;
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
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService service;
    private final TrainerMapper mapper;
    private final TrainingService trainingService;
    private final TrainingMapper trainingMapper;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<CredentialsResponse> create(@RequestBody @Valid CreateTrainerRequest request) {
        var dto = mapper.mapCreateTrainerDto(request);
        var trainer = service.create(dto);
        var user = mapper.mapUserDetailsDto(trainer);
        var credentials = mapper.mapCredentialsResponse(trainer, jwtService.generate(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials);
    }

    @GetMapping("/{username}")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<TrainerResponse> get(@PathVariable("username") String username) {
        var trainer = service.getByUsername(username);
        var response = mapper.mapTrainerResponse(trainer);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<TrainerResponse> update(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateTrainerRequest request
    ) {
        var updateDto = mapper.mapUpdateTraineeDto(username, request);
        var trainer = service.update(updateDto);
        var response = mapper.mapTrainerResponse(trainer);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<List<ShortTrainerDto>> getAllNotAssigned(
            @RequestParam("unassigned") String username
    ) {
        var trainers = service.getAllNotAssignedByTraineeUsername(username);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{username}/trainings")
    @Authorize(UserAuthorizer.Username.class)
    public ResponseEntity<List<TrainingResponse>> getTrainings(
            @PathVariable("username") String username,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "trainee", required = false) String trainee
    ) {
        var filter = mapper.mapCriteriaTrainingTrainerDto(username, from, to, trainee);
        var trainings = trainingService.getAllByTrainerUsernameCriteria(filter);
        var response = trainings.stream().map(trainingMapper::mapTrainingResponse).toList();
        return ResponseEntity.ok(response);
    }

}
