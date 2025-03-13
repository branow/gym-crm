package dev.branow.services;

import dev.branow.annotations.Log;
import dev.branow.dtos.service.CreateTraineeDto;
import dev.branow.dtos.service.ShortTrainerDto;
import dev.branow.dtos.service.TraineeDto;
import dev.branow.dtos.service.UpdateFavoriteTrainersDto;
import dev.branow.dtos.service.UpdateTraineeDto;
import dev.branow.mappers.TraineeMapper;
import dev.branow.mappers.TrainerMapper;
import dev.branow.model.Trainer;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeService {

    private final TraineeRepository repository;
    private final TraineeMapper mapper;
    private final TrainerMapper trainerMapper;
    private final UserService userService;
    private final TrainerRepository trainerRepository;

    @Transactional
    @Log("getting trainee by username %0")
    public TraineeDto getByUsername(String username) {
        return mapper.mapTraineeDto(repository.getReferenceByUsername(username));
    }

    @Log("creating trainee with %0")
    public TraineeDto create(CreateTraineeDto dto) {
        var trainee = mapper.mapTrainee(dto);
        var password = userService.prepareUserForCreation(trainee);
        var savedTrainee = repository.save(trainee);
        var traineeDto = mapper.mapTraineeDto(savedTrainee);
        traineeDto.setPassword(password);
        return traineeDto;
    }

    @Transactional
    @Log("updating trainee with %0")
    public TraineeDto update(UpdateTraineeDto dto) {
        var trainee = repository.getReferenceByUsername(dto.getUsername());
        userService.applyUserUpdates(trainee, dto);
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());
        return mapper.mapTraineeDto(trainee);
    }

    @Transactional
    @Log("deleting trainee by username %0")
    public void deleteByUsername(String username) {
        repository.deleteByUsername(username);
    }

    @Transactional
    @Log("updating favorite trainer list %0")
    public List<ShortTrainerDto> updateFavoriteTrainers(UpdateFavoriteTrainersDto dto) {
        var trainerUsernames = dto.getTrainers();
        var trainee = repository.getReferenceByUsername(dto.getTrainee());

        List<Trainer> currentTrainers = trainee.getFavoriteTrainers();
        Map<String, Trainer> currentTrainersMap = currentTrainers.stream()
                .collect(Collectors.toMap(Trainer::getUsername, Function.identity()));

        List<Trainer> addTrainers = trainerUsernames.stream()
                .filter(username -> !currentTrainersMap.containsKey(username))
                .map((username) -> trainerRepository.findByUsername(username)
                        .orElseThrow(() -> new ValidationException("Trainer not found by identifier " + username)))
                .toList();

        List<Trainer> removeTrainers = currentTrainers.stream()
                .filter(trainer -> !trainerUsernames.contains(trainer.getUsername()))
                .toList();

        currentTrainers.addAll(addTrainers);
        currentTrainers.removeAll(removeTrainers);

        return currentTrainers.stream()
                .map(trainerMapper::mapShortTrainerDto)
                .toList();
    }

}
