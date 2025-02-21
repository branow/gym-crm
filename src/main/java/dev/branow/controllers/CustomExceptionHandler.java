package dev.branow.controllers;

import dev.branow.dtos.response.ErrorResponse;
import dev.branow.exceptions.AccessDeniedException;
import dev.branow.exceptions.BadCredentialsException;
import jakarta.validation.ValidationException;
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

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleObjectNotFound(ObjectNotFoundException ex) {
        var message = String.format("No entity %s found by identifier %s", ex.getEntityName(), ex.getIdentifier());
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title("Entity Not Found")
                .message(message)
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
                .title("Validation Error")
                .message("Invalid request content")
                .details(errors)
                .build());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .title("Validation Error")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .title("Bad Credentials")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .title("Access Denied")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Bad Request")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .title("Method Not Supported")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .title("Media Type Not Supported")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .title("Media Type Not Acceptable")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Missing Path Variable")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Missing Request Parameter")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Missing Request Part")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponse> handleServletRequestBinding(ServletRequestBindingException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Request Binding Error")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title("Endpoint Not Found")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .title("Validation Error")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex) {
        System.err.println("Uncaught exception " + ex.getClass().getSimpleName() + " : " + ex.getMessage());
        return buildResponse(ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Internal Server Error")
                .message(ex.getMessage())
                .build());
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorResponse errorResponse) {
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

}
