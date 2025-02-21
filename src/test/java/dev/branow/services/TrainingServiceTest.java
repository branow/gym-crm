package dev.branow.services;

import dev.branow.DBTest;
import dev.branow.dtos.service.CreateTrainingDto;
import dev.branow.dtos.service.CriteriaTrainingTraineeDto;
import dev.branow.dtos.service.CriteriaTrainingTrainerDto;
import dev.branow.mappers.TrainingMapper;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import dev.branow.model.TrainingType;
import dev.branow.repositories.TraineeRepository;
import dev.branow.repositories.TrainerRepository;
import dev.branow.repositories.TrainingRepository;
import dev.branow.repositories.TrainingTypeRepository;
import dev.branow.repositories.criteria.CriteriaTrainingRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig({
        TrainingService.class,
        CriteriaTrainingRepository.class,
        TrainingMapper.class,
        TrainingTypeMapper.class,
        TrainerRepository.class,
        TraineeRepository.class,
})
@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest extends DBTest {

    @Autowired
    private EntityManager manager;
    @Autowired
    private TrainingMapper mapper;
    @Autowired
    private TrainingRepository repository;
    @Autowired
    private TrainingService service;

    @Test
    public void testCreate() {
        var trainee = manager.find(Trainee.class, 1L);
        var trainer = manager.find(Trainer.class, 4L);

        var query = String.format("select max(t.id) from %s t", Training.class.getName());
        var expectedId = manager.createQuery(query, Long.class).getSingleResult() + 1;

        var createDto = CreateTrainingDto.builder()
                .trainee(trainee.getUsername())
                .trainer(trainer.getUsername())
                .name("training name")
                .date(LocalDate.now())
                .duration(30)
                .build();

        var training = mapper.toTraining(createDto);
        training.setId(expectedId);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(trainer.getSpecialization());

        var actual = service.create(createDto);
        var expected = mapper.toTrainingDto(training);
        assertEquals(expected, actual);
    }

    @Test
    public void getAllByTraineeUsernameCriteria() {
        var dto = CriteriaTrainingTraineeDto.builder()
                .traineeUsername("trainee")
                .trainerUsername("trainer")
                .from(LocalDate.of(2011, 1, 2))
                .to(LocalDate.of(2031, 1, 2))
                .typeId(1L)
                .build();

        var actual = service.getAllByTraineeUsernameCriteria(dto);
        var expected = repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                dto.getTypeId())
                .stream()
                .map(mapper::toTrainingDto)
                .toList();
        assertEquals(expected, actual);
    }

    @Test
    public void getAllByTrainerUsernameCriteria() {
        var dto = CriteriaTrainingTrainerDto.builder()
                .traineeUsername("trainee")
                .trainerUsername("trainer")
                .from(LocalDate.of(2011, 1, 2))
                .to(LocalDate.of(2031, 1, 2))
                .build();

        var actual = service.getAllByTrainerUsernameCriteria(dto);
        var expected = repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                null)
                .stream()
                .map(mapper::toTrainingDto)
                .toList();
        assertEquals(expected, actual);
    }

}
