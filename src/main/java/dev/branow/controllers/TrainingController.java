package dev.branow.controllers;

import dev.branow.annotations.Authenticate;
import dev.branow.annotations.Authorize;
import dev.branow.auth.authorizers.UserAuthorizer;
import dev.branow.dtos.request.CreateTrainingRequest;
import dev.branow.mappers.TrainingMapper;
import dev.branow.services.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/trainings")
public class TrainingController {

    private final TrainingService service;
    private final TrainingMapper mapper;

    @Authenticate
    @Authorize(UserAuthorizer.CreateTrainingRequest.class)
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody @Valid CreateTrainingRequest request
    ) {
        var createDto = mapper.toCreateTrainingDto(request);
        service.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
