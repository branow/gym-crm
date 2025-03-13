package dev.branow.services;

import dev.branow.annotations.Log;
import dev.branow.dtos.service.CreateTrainerDto;
import dev.branow.dtos.service.ShortTrainerDto;
import dev.branow.dtos.service.TrainerDto;
import dev.branow.dtos.service.UpdateTrainerDto;
import dev.branow.mappers.TrainerMapper;
import dev.branow.model.Trainer;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository repository;
    private final TrainerMapper mapper;
    private final UserService userService;
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    @Log("getting all not assigned trainers on trainee by username %0")
    public List<ShortTrainerDto> getAllNotAssignedByTraineeUsername(String username) {
        return repository.findAllNotAssignedOnTraineeByTraineeUsername(username).stream()
                .filter(Trainer::getIsActive)
                .map(mapper::mapShortTrainerDto).toList();
    }

    @Transactional
    @Log("getting trainer by username %0")
    public TrainerDto getByUsername(String username) {
        return mapper.mapTrainerDto(repository.getReferenceByUsername(username));
    }

    @Transactional
    @Log("creating trainer with %0")
    public TrainerDto create(CreateTrainerDto dto) {
        var trainer = mapper.mapTrainer(dto);
        var password = userService.prepareUserForCreation(trainer);
        var trainingType = trainingTypeRepository.findById(dto.getSpecialization())
                .orElseThrow(() -> new ValidationException("Specialization not found by identifier " + dto.getSpecialization()));
        trainer.setSpecialization(trainingType);
        var savedTrainer = repository.save(trainer);
        var trainerDto = mapper.mapTrainerDto(savedTrainer);
        trainerDto.setPassword(password);
        return trainerDto;
    }

    @Transactional
    @Log("updating trainer with %0")
    public TrainerDto update(UpdateTrainerDto dto) {
        var trainer = repository.getReferenceByUsername(dto.getUsername());
        var trainingType = trainingTypeRepository.findById(dto.getSpecialization())
                .orElseThrow(() -> new ValidationException("Specialization not found by identifier " + dto.getSpecialization()));
        trainer.setSpecialization(trainingType);
        userService.applyUserUpdates(trainer, dto);
        return mapper.mapTrainerDto(trainer);
    }

}
