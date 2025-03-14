package dev.branow.controllers;

import dev.branow.dtos.request.CreateTrainerRequest;
import dev.branow.dtos.request.UpdateTrainerRequest;
import dev.branow.dtos.service.CriteriaTrainingTrainerDto;
import dev.branow.mappers.TraineeTrainerMapper;
import dev.branow.mappers.TrainerMapper;
import dev.branow.mappers.TrainingMapper;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.security.JwtService;
import dev.branow.services.TrainerService;
import dev.branow.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.branow.TestDataFactory.*;
import static dev.branow.controllers.JsonMapper.toJson;
import static dev.branow.controllers.RestUtils.rest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({
        TrainerController.class,
        CustomExceptionHandler.class,
        TrainerMapper.class,
        TrainingMapper.class,
        TrainingTypeMapper.class,
        TraineeTrainerMapper.class,
})
public class TrainerControllerTest {

    @MockitoBean
    private TrainerService service;
    @MockitoBean
    private TrainingService trainingService;
    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private TrainingTypeMapper trainingTypeMapper;
    @Autowired
    private TrainingMapper trainingMapper;
    @Autowired
    private TrainerMapper mapper;
    @Autowired
    private TrainerController controller;
    @Autowired
    private CustomExceptionHandler errorHandler;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    public void testCreate() throws Exception {
        var jwt = "token";
        var trainerDto = mapper.mapTrainerDto(nextTrainer(nextTrainingType(), null));
        var createTrainerRequest = CreateTrainerRequest.builder()
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .specialization(trainerDto.getSpecialization().getId())
                .build();
        var createTrainerDto = mapper.mapCreateTrainerDto(createTrainerRequest);
        var credentialsResponse = mapper.mapCredentialsResponse(trainerDto, jwt);

        when(service.create(createTrainerDto)).thenReturn(trainerDto);
        when(jwtService.generate(any(UserDetails.class))).thenReturn(jwt);

        var request = rest(post("/trainers")).content(toJson(createTrainerRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(toJson(credentialsResponse)));
    }

    @ParameterizedTest
    @MethodSource("provideTestCreate_invalidTrainer_return422")
    public void testCreate_invalidTrainer_return422(CreateTrainerRequest content) throws Exception {
        var request = rest(post("/trainers")).content(toJson(content));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        verify(service, never()).create(any());
    }

    private static Stream<CreateTrainerRequest> provideTestCreate_invalidTrainer_return422() {
        return Stream.of(
                CreateTrainerRequest.builder().firstName(null).lastName("Doe").specialization(1L).build(),
                CreateTrainerRequest.builder().firstName(null).lastName("Doe").specialization(1L).build(),
                CreateTrainerRequest.builder().firstName("John").lastName(null).specialization(1L).build(),
                CreateTrainerRequest.builder().firstName("").lastName("Doe").specialization(1L).build(),
                CreateTrainerRequest.builder().firstName("John").lastName("").specialization(1L).build(),
                CreateTrainerRequest.builder().firstName("a").lastName("Doe").specialization(1L).build(),
                CreateTrainerRequest.builder().firstName("John").lastName("a").specialization(1L).build(),
                CreateTrainerRequest.builder().firstName("a".repeat(46)).specialization(1L).lastName("Doe").build(),
                CreateTrainerRequest.builder().firstName("John").lastName("a".repeat(46)).specialization(1L).build(),
                CreateTrainerRequest.builder().firstName("John").lastName("Doe").specialization(null).build(),
                CreateTrainerRequest.builder().firstName("John").lastName("Doe").specialization(-1L).build()
        );
    }

    @Test
    public void testGet() throws Exception {
        var trainee = nextTrainee(null);
        var trainer = nextTrainer(nextTrainingType(), null);
        var trainings = List.of(
                nextTraining(trainer.getSpecialization(), trainee, trainer),
                nextTraining(trainer.getSpecialization(), trainee, trainer)
        );
        trainer.setTrainings(trainings);

        var trainerDto = mapper.mapTrainerDto(trainer);
        var trainerResponse = mapper.mapTrainerResponse(trainerDto);

        when(service.getByUsername(trainer.getUsername())).thenReturn(trainerDto);

        var request = rest(get("/trainers/{username}", trainer.getUsername()));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(trainerResponse)));
    }

    @Test
    public void testUpdate() throws Exception {
        var trainerDto = mapper.mapTrainerDto(nextTrainer(nextTrainingType(), null));
        var updateTrainerRequest = UpdateTrainerRequest.builder()
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .specialization(2L)
                .isActive(true)
                .build();
        var updateTrainerDto = mapper.mapUpdateTraineeDto(trainerDto.getUsername(), updateTrainerRequest);
        var trainerResponse = mapper.mapTrainerResponse(trainerDto);

        when(service.update(updateTrainerDto)).thenReturn(trainerDto);

        var request = rest(put("/trainers/{username}", updateTrainerDto.getUsername()))
                .content(toJson(updateTrainerRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(trainerResponse)));
    }

    @ParameterizedTest
    @MethodSource("provideTestUpdate_invalidTrainer_return422")
    public void testUpdate_invalidTrainer_return422(UpdateTrainerRequest updateRequest) throws Exception {
        var request = rest(put("/trainers/John.Doe"))
                .content(toJson(updateRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        verify(service, never()).update(any());
    }

    private static Stream<UpdateTrainerRequest> provideTestUpdate_invalidTrainer_return422() {
        return Stream.of(
                UpdateTrainerRequest.builder().firstName(null).lastName("Doe").isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("").lastName("Doe").isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("a").lastName("Doe").isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("a".repeat(46)).lastName("Doe").isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("John").lastName(null).isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("John").lastName("").isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("John").lastName("a").isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("John").lastName("a".repeat(46)).isActive(false).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("John").lastName("Doe").isActive(null).specialization(0L).build(),
                UpdateTrainerRequest.builder().firstName("John").lastName("Doe").isActive(true).specialization(null).build(),
                UpdateTrainerRequest.builder().firstName("John").lastName("Doe").isActive(true).specialization(-1L).build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestGetTrainings")
    public void testGetTrainings(CriteriaTrainingTrainerDto criteria) throws Exception {
        var trainingDtos = Stream.of(
                        nextTraining(null, null, null),
                        nextTraining(null, null, null)
                ).map(trainingMapper::mapTrainingDto)
                .toList();
        var trainingResponse = trainingDtos.stream().map(trainingMapper::mapTrainingResponse).toList();

        when(trainingService.getAllByTrainerUsernameCriteria(criteria)).thenReturn(trainingDtos);

        String params = Map.of(
                        "from", Optional.ofNullable(criteria.getFrom()).map(LocalDate::toString).orElse(""),
                        "to", Optional.ofNullable(criteria.getTo()).map(LocalDate::toString).orElse(""),
                        "trainee", Optional.ofNullable(criteria.getTraineeUsername()).orElse("")
                ).entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        var uri = String.format("/trainers/%s/trainings?%s", criteria.getTrainerUsername(), params);

        mockMvc.perform(rest(get(uri)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(trainingResponse)));
    }

    private static Stream<CriteriaTrainingTrainerDto> provideTestGetTrainings() {
        return Stream.of(
                CriteriaTrainingTrainerDto.builder()
                        .trainerUsername("John.Snow")
                        .build(),
                CriteriaTrainingTrainerDto.builder()
                        .trainerUsername("John.Snow")
                        .from(LocalDate.now())
                        .build(),
                CriteriaTrainingTrainerDto.builder()
                        .trainerUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now()).build(),
                CriteriaTrainingTrainerDto.builder()
                        .trainerUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now())
                        .build(),
                CriteriaTrainingTrainerDto.builder()
                        .trainerUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now())
                        .build(),
                CriteriaTrainingTrainerDto.builder()
                        .trainerUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now())
                        .traineeUsername("Bob.Doe")
                        .build()
        );
    }

    @Test
    public void testGetAllNotAssigned() throws Exception {
        var username = "John.Snow";
        var shortTrainerDtos = Stream.of(
                nextTrainer(nextTrainingType(), null),
                nextTrainer(nextTrainingType(), null),
                nextTrainer(nextTrainingType(), null)
        ).map(mapper::mapShortTrainerDto).toList();

        when(service.getAllNotAssignedByTraineeUsername(username)).thenReturn(shortTrainerDtos);

        var request = rest(get("/trainers?unassigned={username}", username));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(shortTrainerDtos)));
    }

}
