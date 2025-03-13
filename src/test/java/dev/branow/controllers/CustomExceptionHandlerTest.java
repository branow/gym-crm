package dev.branow.controllers;

import dev.branow.dtos.response.ErrorResponse;
import dev.branow.model.User;
import jakarta.validation.ValidationException;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.stream.Stream;

import static dev.branow.controllers.JsonMapper.fromJson;
import static dev.branow.controllers.RestUtils.rest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig({
        CustomExceptionHandler.class,
        CustomExceptionHandlerTest.TestController.class,
})
public class CustomExceptionHandlerTest {

    @RestController
    public static class TestController {
        Exception exception;

        @GetMapping
        public ResponseEntity<?> get() throws Exception {
            throw exception;
        }

        public void testMethod(String data) {}
    }

    @Autowired
    private TestController controller;
    @Autowired
    private CustomExceptionHandler handler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideTestHandle")
    public void testHandle(Exception exception, ErrorResponse response) throws Exception {
        controller.exception = exception;
        mockMvc.perform(rest(get("/")))
                .andDo(print())
                .andExpect(status().is(response.getStatus()))
                .andExpect((result) -> {
                    var actualJson = result.getResponse().getContentAsString();
                    var actualResponse = fromJson(actualJson, ErrorResponse.class);
                    response.setTimestamp(actualResponse.getTimestamp());
                    assertEquals(response, actualResponse);
                });
    }

    private static Stream<Arguments> provideTestHandle() throws NoSuchMethodException {
        return Stream.of(
                Arguments.of(
                        new Exception("Unknown exception"),
                        ErrorResponse.builder()
                                .status(500)
                                .title("Internal Server Error")
                                .message("Unknown exception")
                                .build()
                ),
                Arguments.of(
                        new HttpRequestMethodNotSupportedException("GET"),
                        ErrorResponse.builder()
                                .status(405)
                                .title("Method Not Supported")
                                .message("Request method 'GET' is not supported")
                                .build()
                ),
                Arguments.of(
                        new HttpMediaTypeNotSupportedException("Exception message"),
                        ErrorResponse.builder()
                                .status(415)
                                .title("Media Type Not Supported")
                                .message("Exception message")
                                .build()
                ),
                Arguments.of(
                        new HttpMediaTypeNotAcceptableException("Exception message"),
                        ErrorResponse.builder()
                                .status(406)
                                .title("Media Type Not Acceptable")
                                .message("Exception message")
                                .build()
                ),
                Arguments.of(
                        new MissingPathVariableException("data", new MethodParameter(TestController.class.getMethod("testMethod", String.class), 0)),
                        ErrorResponse.builder()
                                .status(400)
                                .title("Missing Path Variable")
                                .message("Required URI template variable 'data' for method parameter type String is not present")
                                .build()
                ),
                Arguments.of(
                        new MissingServletRequestParameterException("data", "String"),
                        ErrorResponse.builder()
                                .status(400)
                                .title("Missing Request Parameter")
                                .message("Required request parameter 'data' for method parameter type String is not present")
                                .build()
                ),
                Arguments.of(
                        new MissingServletRequestPartException("message"),
                        ErrorResponse.builder()
                                .status(400)
                                .title("Missing Request Part")
                                .message("Required part 'message' is not present.")
                                .build()
                ),
                Arguments.of(
                        new ServletRequestBindingException("Exception message"),
                        ErrorResponse.builder()
                                .status(400)
                                .title("Request Binding Error")
                                .message("Exception message")
                                .build()
                ),
                Arguments.of(
                        new NoHandlerFoundException("GET", "/home", new HttpHeaders()),
                        ErrorResponse.builder()
                                .status(404)
                                .title("Endpoint Not Found")
                                .message("No endpoint GET /home.")
                                .build()
                ),
                Arguments.of(
                        new HttpMessageNotReadableException("message", (HttpInputMessage) null),
                        ErrorResponse.builder()
                                .status(422)
                                .title("Validation Error")
                                .message("message")
                                .build()
                ),
                Arguments.of(
                        new ObjectNotFoundException(User.class.getSimpleName(), 2L),
                        ErrorResponse.builder()
                                .status(404)
                                .title("Entity Not Found")
                                .message("No entity User found by identifier 2")
                                .build()
                ),
                Arguments.of(
                        new IllegalArgumentException("Invalid Argument"),
                        ErrorResponse.builder()
                                .status(400)
                                .title("Bad Request")
                                .message("Invalid Argument")
                                .build()
                ),
                Arguments.of(
                        new ValidationException("Passwords do not match"),
                        ErrorResponse.builder()
                                .status(422)
                                .title("Validation Error")
                                .message("Passwords do not match")
                                .build()
                ),
                Arguments.of(
                        new MethodArgumentNotValidException(
                                new MethodParameter(TestController.class.getMethod("testMethod", String.class), 0),
                                getBindingResult(
                                        "user",
                                        Map.of(
                                                "email", "Invalid email format"
                                        )
                                )
                        ),
                        ErrorResponse.builder()
                                .status(422)
                                .title("Validation Error")
                                .message("Invalid request content")
                                .details(Map.of(
                                        "email", "Invalid email format"
                                ))
                                .build()
                )
        );
    }

    private static BindingResult getBindingResult(String objectName, Map<String, String> fieldErrors) {
        BindingResult bindingResult = new BeanPropertyBindingResult(null, objectName);
        for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
            bindingResult.addError(new FieldError(objectName, entry.getKey(), entry.getValue()));
        }
        return bindingResult;
    }

}
