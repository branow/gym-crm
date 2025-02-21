package dev.branow.controllers;

import dev.branow.dtos.request.CreateTrainingRequest;
import dev.branow.mappers.TrainingMapper;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.stream.Stream;

import static dev.branow.controllers.JsonMapper.toJson;
import static dev.branow.controllers.RestUtils.rest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({
        TrainingController.class,
        CustomExceptionHandler.class,
        TrainingMapper.class,
        TrainingTypeMapper.class,
})
public class TrainingControllerTest {

    @MockitoBean
    private TrainingService service;

    @Autowired
    private TrainingMapper mapper;
    @Autowired
    private TrainingController controller;
    @Autowired
    private CustomExceptionHandler exceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    public void testCreate() throws Exception {
        var createTrainingRequest = CreateTrainingRequest.builder()
                .name("test training")
                .date(LocalDate.now())
                .duration(12)
                .trainee("test trainee")
                .trainer("test trainer")
                .build();
        var createTrainingDto = mapper.toCreateTrainingDto(createTrainingRequest);

        var request = rest(post("/trainings")).content(toJson(createTrainingDto));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("provideTestCreate_invalidTraining_return42")
    public void testCreate_invalidTraining_return422(CreateTrainingRequest createTrainingRequest) throws Exception {
        var createTrainingDto = mapper.toCreateTrainingDto(createTrainingRequest);

        var request = rest(post("/trainings")).content(toJson(createTrainingDto));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    private static Stream<CreateTrainingRequest> provideTestCreate_invalidTraining_return42() {
        return Stream.of(
                CreateTrainingRequest.builder()
                        .trainee(null)
                        .trainer("Bob")
                        .name("fitness")
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("")
                        .trainer("Bob")
                        .name("fitness")
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer(null)
                        .name("fitness")
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("")
                        .name("fitness")
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name(null)
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name("")
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name("f")
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name("f".repeat(101))
                        .date(LocalDate.now())
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name("fitness")
                        .date(null)
                        .duration(45)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name("fitness")
                        .date(LocalDate.now())
                        .duration(null)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name("fitness")
                        .date(LocalDate.now())
                        .duration(-1)
                        .build(),
                CreateTrainingRequest.builder()
                        .trainee("John")
                        .trainer("Bob")
                        .name("fitness")
                        .date(LocalDate.now())
                        .duration(0)
                        .build()
        );
    }

}
