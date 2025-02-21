package dev.branow.controllers;

import dev.branow.dtos.request.CreateTraineeRequest;
import dev.branow.dtos.request.UpdateFavoriteTrainersRequest;
import dev.branow.dtos.request.UpdateTraineeRequest;
import dev.branow.dtos.service.CriteriaTrainingTraineeDto;
import dev.branow.mappers.*;
import dev.branow.model.Trainer;
import dev.branow.services.TraineeService;
import dev.branow.services.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
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
import static dev.branow.controllers.JsonMapper.*;
import static dev.branow.controllers.RestUtils.rest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({
        TraineeController.class,
        CustomExceptionHandler.class,
        TraineeMapper.class,
        TrainerMapper.class,
        TrainingMapper.class,
        TrainingTypeMapper.class,
        TraineeTrainerMapper.class,
})
public class TraineeControllerTest {

    @MockitoBean
    private TraineeService service;
    @MockitoBean
    private TrainingService trainingService;

    @Autowired
    private TraineeTrainerMapper traineeTrainerMapper;
    @Autowired
    private TraineeMapper mapper;
    @Autowired
    private TraineeController controller;

    private MockMvc mockMvc;
    @Autowired
    private TrainingMapper trainingMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    public void testCreate() throws Exception {
        var traineeDto = mapper.toTraineeDto(nextTrainee(null));
        var createTraineeRequest = CreateTraineeRequest.builder()
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .address(traineeDto.getAddress())
                .dateOfBirth(traineeDto.getDateOfBirth())
                .build();
        var createTraineeDto = mapper.toCreateTraineeDto(createTraineeRequest);
        var credentialsResponse = mapper.toCredentialsResponse(traineeDto);

        when(service.create(createTraineeDto)).thenReturn(traineeDto);

        var request = rest(post("/trainees")).content(toJson(createTraineeRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(toJson(credentialsResponse)));
    }

    @ParameterizedTest
    @MethodSource("provideTestCreate_invalidTrainee_return422")
    public void testCreate_invalidTrainee_return422(CreateTraineeRequest content) throws Exception {
        var request = rest(post("/trainees")).content(toJson(content));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        verify(service, never()).create(any());
    }

    private static Stream<CreateTraineeRequest> provideTestCreate_invalidTrainee_return422() {
        return Stream.of(
                CreateTraineeRequest.builder().firstName(null).lastName("Doe").build(),
                CreateTraineeRequest.builder().firstName(null).lastName("Doe").build(),
                CreateTraineeRequest.builder().firstName("John").lastName(null).build(),
                CreateTraineeRequest.builder().firstName("").lastName("Doe").build(),
                CreateTraineeRequest.builder().firstName("John").lastName("").build(),
                CreateTraineeRequest.builder().firstName("a").lastName("Doe").build(),
                CreateTraineeRequest.builder().firstName("John").lastName("a").build(),
                CreateTraineeRequest.builder().firstName("a".repeat(46)).lastName("Doe").build(),
                CreateTraineeRequest.builder().firstName("John").lastName("a".repeat(46)).build(),
                CreateTraineeRequest.builder().firstName("John").lastName("Doe").dateOfBirth(LocalDate.now().plusDays(1)).build(),
                CreateTraineeRequest.builder().firstName("John").lastName("Doe").address("a".repeat(256)).build()
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
        trainee.setTrainings(trainings);

        var traineeDto = mapper.toTraineeDto(trainee);
        var traineeResponse = mapper.toTraineeResponse(traineeDto);

        when(service.getByUsername(trainee.getUsername())).thenReturn(traineeDto);

        var request = rest(get("/trainees/{username}", trainee.getUsername()));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(traineeResponse)));
    }

    @Test
    public void testUpdate() throws Exception {
        var traineeDto = mapper.toTraineeDto(nextTrainee(null));
        var updateTraineeRequest = UpdateTraineeRequest.builder()
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .isActive(true)
                .build();
        var updateDto = mapper.toUpdateTraineeDto(traineeDto.getUsername(), updateTraineeRequest);
        var traineeResponse = mapper.toTraineeResponse(traineeDto);

        when(service.update(updateDto)).thenReturn(traineeDto);

        var request = rest(put("/trainees/{username}", updateDto.getUsername()))
                .content(toJson(updateTraineeRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(traineeResponse)));
    }

    @ParameterizedTest
    @MethodSource("provideTestUpdate_invalidTrainee_return422")
    public void testUpdate_invalidTrainee_return422(UpdateTraineeRequest updateRequest) throws Exception {
        var request = rest(put("/trainees/John.Doe"))
                .content(toJson(updateRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        verify(service, never()).update(any());
    }

    private static Stream<UpdateTraineeRequest> provideTestUpdate_invalidTrainee_return422() {
        return Stream.of(
                UpdateTraineeRequest.builder().firstName(null).lastName("Doe").isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("").lastName("Doe").isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("a").lastName("Doe").isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("a".repeat(46)).lastName("Doe").isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("John").lastName(null).isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("John").lastName("").isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("John").lastName("a").isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("John").lastName("a".repeat(46)).isActive(false).build(),
                UpdateTraineeRequest.builder().firstName("John").lastName("Doe").isActive(null).build(),
                UpdateTraineeRequest.builder().firstName("John").lastName("Doe").isActive(true).dateOfBirth(LocalDate.now().plusDays(1)).build(),
                UpdateTraineeRequest.builder().firstName("John").lastName("Doe").isActive(true).address("a".repeat(256)).build()
        );
    }

    @Test
    public void testDelete() throws Exception {
        var username = "John.Doe";
        var request = rest(delete("/trainees/{username}", username));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(service, times(1)).deleteByUsername(username);
    }

    @Test
    public void testUpdateFavoriteTrainers() throws Exception {
        var username = "John.Doe";
        var specialization = nextTrainingType();
        var trainers = List.of(
                nextTrainer(specialization, null),
                nextTrainer(specialization, null),
                nextTrainer(specialization, null)
        );
        var shortTrainerDtos = trainers.stream().map(traineeTrainerMapper::toShortTrainerDto).toList();
        var trainerUsernames = trainers.stream().map(Trainer::getUsername).toList();
        var updateFavoriteTrainersRequest = new UpdateFavoriteTrainersRequest(trainerUsernames);
        var updateFavoriteTrainersDto = mapper.toUpdateFavouriteTrainersDto(username, updateFavoriteTrainersRequest);

        when(service.updateFavoriteTrainers(updateFavoriteTrainersDto)).thenReturn(shortTrainerDtos);

        var request = rest(put("/trainees/{username}/favorite-trainers", username))
                .content(toJson(updateFavoriteTrainersRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(shortTrainerDtos)));
    }

    @Test
    public void testUpdateFavoriteTrainers_nullFavoriteTrainerList_return422() throws Exception {
        var updateFavoriteTrainersRequest = new UpdateFavoriteTrainersRequest(null);

        var request = rest(put("/trainees/John.Doe/favorite-trainers"))
                .content(toJson(updateFavoriteTrainersRequest));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        verify(service, never()).updateFavoriteTrainers(any());
    }

    @ParameterizedTest
    @MethodSource("provideTestGetTrainings")
    public void testGetTrainings(CriteriaTrainingTraineeDto criteria) throws Exception {
        var trainingDtos = Stream.of(
                nextTraining(null, null, null),
                nextTraining(null, null, null)
                ).map(trainingMapper::toTrainingDto)
                .toList();
        var trainingResponse = trainingDtos.stream().map(trainingMapper::toTrainingResponse).toList();

        when(trainingService.getAllByTraineeUsernameCriteria(criteria)).thenReturn(trainingDtos);

        String params = Map.of(
                        "from", Optional.ofNullable(criteria.getFrom()).map(LocalDate::toString).orElse(""),
                        "to", Optional.ofNullable(criteria.getTo()).map(LocalDate::toString).orElse(""),
                        "type", Optional.ofNullable(criteria.getTypeId()).map(String::valueOf).orElse(""),
                        "trainer", Optional.ofNullable(criteria.getTrainerUsername()).orElse("")
                ).entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        var uri = String.format("/trainees/%s/trainings?%s", criteria.getTraineeUsername(), params);

        mockMvc.perform(rest(get(uri)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(trainingResponse)));
    }

    private static Stream<CriteriaTrainingTraineeDto> provideTestGetTrainings() {
        return Stream.of(
                CriteriaTrainingTraineeDto.builder()
                        .traineeUsername("John.Snow")
                        .build(),
                CriteriaTrainingTraineeDto.builder()
                        .traineeUsername("John.Snow")
                        .from(LocalDate.now())
                        .build(),
                CriteriaTrainingTraineeDto.builder()
                        .traineeUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now()).build(),
                CriteriaTrainingTraineeDto.builder()
                        .traineeUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now())
                        .build(),
                CriteriaTrainingTraineeDto.builder()
                        .traineeUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now())
                        .typeId(1L)
                        .build(),
                CriteriaTrainingTraineeDto.builder()
                        .traineeUsername("John.Snow")
                        .from(LocalDate.now())
                        .to(LocalDate.now())
                        .typeId(1L)
                        .trainerUsername("Bob.Doe")
                        .build()
        );
    }

}
