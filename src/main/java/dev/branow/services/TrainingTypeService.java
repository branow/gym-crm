package dev.branow.services;

import dev.branow.exceptions.EntityNotFoundException;
import dev.branow.model.TrainingType;
import dev.branow.repositories.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {

    private final TrainingTypeRepository repository;

    public TrainingType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TrainingType.class, id));
    }

}
