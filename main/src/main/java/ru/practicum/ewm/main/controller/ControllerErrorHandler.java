package ru.practicum.ewm.main.controller;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.common.dto.ApiError;
import ru.practicum.ewm.main.exception.*;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ApiError> handleException(Exception ex) {
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex, "Main service error.");
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleStatServiceException(StatServiceException ex) {
        return getResponseEntity(HttpStatus.SERVICE_UNAVAILABLE, ex, "Stat service error.");
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleConflictException(ConflictException ex) {
        return getResponseEntity(HttpStatus.CONFLICT, ex,
                "For the requested operation the conditions are not met.");
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.FORBIDDEN,
                "For the requested operation the conditions are not met.", ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
        return getResponseEntity(HttpStatus.NOT_FOUND, ex, "The required object was not found.");
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return getResponseEntity(HttpStatus.CONFLICT, ex, "Integrity constraint has been violated.");
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return getResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex, "Incorrect media type.");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, DateTimeParseException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            NumberFormatException.class, BadRequestException.class})
    public ResponseEntity<ApiError> handleBadRequestException(Exception ex) {
        return getResponseEntity(HttpStatus.BAD_REQUEST, ex, "Incorrectly made request.");
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        return handleValidationError(getFieldsErrors(ex));
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleValidationError(getFieldsErrors(ex));
    }

    private ResponseEntity<ApiError> getResponseEntity(HttpStatus status, Exception ex, String reason) {
        return new ResponseEntity<>(new ApiError(status, reason, ex), status);
    }

    private ResponseEntity<ApiError> handleValidationError(Map<String, FieldValidationErrors> fieldsErrors) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = fieldsErrors.entrySet()
                .stream()
                .map(e -> String.format("Field: %s. Error: %s. Value: %s", e.getKey(),
                        String.join("; ", e.getValue().getErrors()), e.getValue().getRejectedValue()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ApiError(status, "Incorrectly made request.", errors), status);
    }

    private Map<String, FieldValidationErrors> getFieldsErrors(ConstraintViolationException ex) {
        Map<String, FieldValidationErrors> fieldsErrors = new TreeMap<>(String::compareTo);

        ex.getConstraintViolations().forEach((constraintViolation -> {
            String field = null;

            for (Path.Node node : constraintViolation.getPropertyPath()) {
                field = node.getName();
            }

            if (field != null) {
                String errorMessage = constraintViolation.getMessage();
                String rejectedValue = constraintViolation.getInvalidValue() == null ? "null"
                        : constraintViolation.getInvalidValue().toString();

                if (!fieldsErrors.containsKey(field)) {
                    fieldsErrors.put(field, new FieldValidationErrors(rejectedValue));
                }

                fieldsErrors.get(field).getErrors().add(errorMessage);
            }
        }));

        return fieldsErrors;
    }

    private Map<String, FieldValidationErrors> getFieldsErrors(MethodArgumentNotValidException ex) {
        Map<String, FieldValidationErrors> fieldsErrors = new TreeMap<>(String::compareTo);

        ex.getBindingResult().getAllErrors().forEach(error -> {
            FieldError fieldError = (FieldError) error;

            String field = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            String rejectedValue = fieldError.getRejectedValue() == null ? "null"
                    : fieldError.getRejectedValue().toString();

            if (!fieldsErrors.containsKey(field)) {
                fieldsErrors.put(field, new FieldValidationErrors(rejectedValue));
            }

            fieldsErrors.get(field).getErrors().add(errorMessage);
        });

        return fieldsErrors;
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class FieldValidationErrors {
        String rejectedValue;

        Set<String> errors;

        FieldValidationErrors(String rejectedValue) {
            this.rejectedValue = rejectedValue;
            errors = new TreeSet<>(String::compareTo);
        }
    }
}