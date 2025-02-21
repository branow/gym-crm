package dev.branow.services;

import dev.branow.annotations.Log;
import dev.branow.dtos.service.TrainingTypeDto;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.repositories.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {

    private final TrainingTypeRepository repository;
    private final TrainingTypeMapper mapper;

    @Log("getting all training types")
    public List<TrainingTypeDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toTrainingTypeDto)
                .toList();
    }

}
