package dev.branow.services;

import dev.branow.dtos.CreateTrainingDto;
import dev.branow.dtos.CriteriaTrainingTraineeDto;
import dev.branow.dtos.CriteriaTrainingTrainerDto;
import dev.branow.mappers.TrainingMapper;
import dev.branow.model.Trainee;
import dev.branow.model.Trainer;
import dev.branow.model.Training;
import dev.branow.model.TrainingType;
import dev.branow.repositories.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingRepository repository;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingTypeService trainingTypeService;

    private final TrainingMapper mapper = new TrainingMapper();
    private TrainingService service;

    @BeforeEach
    void setUp() {
        service = new TrainingService(repository, mapper, traineeService,
                trainerService, trainingTypeService);
    }

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

        when(trainingTypeService.getById(type.getId())).thenReturn(type);
        when(trainerService.getById(trainer.getId())).thenReturn(trainer);
        when(traineeService.getById(trainee.getId())).thenReturn(trainee);
        when(repository.save(training)).thenReturn(training);

        var actual = service.create(createDto);
        assertEquals(training, actual);
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
        assertEquals(trainings, actual);
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
        assertEquals(trainings, actual);
    }









}
