package dev.branow.controllers;

import dev.branow.dtos.response.TrainingTypeResponse;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.services.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {

    private final TrainingTypeService service;
    private final TrainingTypeMapper mapper;

    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAll() {
        var types = service.getAll().stream()
                .map(mapper::mapTrainingTypeResponse)
                .toList();
        return ResponseEntity.ok(types);
    }

}
