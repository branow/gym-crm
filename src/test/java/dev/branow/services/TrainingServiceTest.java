package dev.branow.services;

import dev.branow.dtos.*;
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
        TrainingMapper.class,
        TrainingTypeMapper.class,
        TrainerRepository.class,
        TraineeRepository.class,
})
@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @MockitoBean
    private TrainingRepository repository;
    @MockitoBean
    private TraineeRepository traineeRepository;
    @MockitoBean
    private TrainerRepository trainerRepository;
    @MockitoBean
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private TrainingMapper mapper;
    @Autowired
    private TrainingService service;


    @Test
    public void testCreate() {
        var trainer = new Trainer();
        trainer.setId(12L);
        trainer.setUsername("trainer");

        var trainee = new Trainee();
        trainee.setId(15L);
        trainee.setUsername("trainee");

        var type = new TrainingType();
        type.setId(1L);
        type.setName("strength");

        var createDto = CreateTrainingDto.builder()
                .traineeId(trainee.getId())
                .trainerId(trainer.getId())
                .typeId(type.getId())
                .build();

        var training = mapper.toTraining(createDto);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(type);

        when(trainingTypeRepository.getReferenceById(type.getId())).thenReturn(type);
        when(trainerRepository.getReferenceById(trainer.getId())).thenReturn(trainer);
        when(traineeRepository.getReferenceById(trainee.getId())).thenReturn(trainee);
        when(repository.save(training)).thenReturn(training);

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
        var trainings = List.of(
                Training.builder().name("strength").build(),
                Training.builder().name("run").build()
        );

        when(repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                dto.getTypeId())
        ).thenReturn(trainings);

        var actual = service.getAllByTraineeUsernameCriteria(dto);
        var expected = trainings.stream().map(mapper::toTrainingDto).toList();
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
        var trainings = List.of(
                Training.builder().name("strength").build(),
                Training.builder().name("run").build()
        );

        when(repository.findAllByCriteria(
                dto.getTraineeUsername(),
                dto.getTrainerUsername(),
                dto.getFrom(),
                dto.getTo(),
                null)
        ).thenReturn(trainings);

        var actual = service.getAllByTrainerUsernameCriteria(dto);
        var expected = trainings.stream().map(mapper::toTrainingDto).toList();
        assertEquals(expected, actual);
    }


}
