package dev.branow.controllers;

import dev.branow.dtos.response.ErrorResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    private final Map<Class<? extends Exception>, String> titles = new HashMap<>();

    {
        titles.put(ObjectNotFoundException.class, "Entity Not Found");
        titles.put(MethodArgumentNotValidException.class, "Validation Error");
        titles.put(ValidationException.class, "Validation Error");
        titles.put(HttpMessageNotReadableException.class, "Validation Error");
        titles.put(IllegalArgumentException.class, "Bad Request");
        titles.put(HttpRequestMethodNotSupportedException.class, "Method Not Supported");
        titles.put(HttpMediaTypeNotSupportedException.class, "Media Type Not Supported");
        titles.put(HttpMediaTypeNotAcceptableException.class, "Media Type Not Acceptable");
        titles.put(MissingPathVariableException.class, "Missing Path Variable");
        titles.put(MissingServletRequestParameterException.class, "Missing Request Parameter");
        titles.put(MissingServletRequestPartException.class, "Missing Request Part");
        titles.put(ServletRequestBindingException.class, "Request Binding Error");
        titles.put(NoHandlerFoundException.class, "Endpoint Not Found");
        titles.put(Exception.class, "Internal Server Error");
    }

    @ExceptionHandler({
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            ServletRequestBindingException.class,
            IllegalArgumentException.class,
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title(titles.get(ex.getClass()))
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title(titles.get(ex.getClass()))
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleObjectNotFound(ObjectNotFoundException ex) {
        var message = String.format("No entity %s found by identifier %s", ex.getEntityName(), ex.getIdentifier());
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title(titles.get(ex.getClass()))
                .message(message)
                .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .title(titles.get(ex.getClass()))
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .title(titles.get(ex.getClass()))
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .title(titles.get(ex.getClass()))
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler({
            ValidationException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleUnprocessableEntity(Exception ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .title(titles.get(ex.getClass()))
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .title(titles.get(ex.getClass()))
                .message("Invalid request content")
                .details(errors)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex) {
        log.error("Uncaught exception {} : {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title(titles.get(ex.getClass()))
                .message(ex.getMessage())
                .build());
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorResponse errorResponse) {
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

}
