package dev.branow.controllers;

import dev.branow.TestDataFactory;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.services.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static dev.branow.controllers.JsonMapper.toJson;
import static dev.branow.controllers.RestUtils.rest;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({
        TrainingTypeController.class,
        CustomExceptionHandler.class,
        TrainingTypeMapper.class,
})
public class TrainingTypeControllerTest {

    @MockitoBean
    private TrainingTypeService service;

    @Autowired
    private TrainingTypeMapper mapper;
    @Autowired
    private TrainingTypeController controller;
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
    public void testGetAll() throws Exception {
        var trainingTypeDtos = Stream.generate(TestDataFactory::nextTrainingType)
                .limit(5).map(mapper::mapTrainingTypeDto).toList();
        var trainingTypeResponses = trainingTypeDtos.stream().map(mapper::mapTrainingTypeResponse).toList();

        when(service.getAll()).thenReturn(trainingTypeDtos);

        mockMvc.perform(rest(get("/training-types")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(trainingTypeResponses)));
    }

}
