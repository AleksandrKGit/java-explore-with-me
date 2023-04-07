package ru.practicum.ewm.stat.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.common.dto.ApiError;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ApiError> handleException(Exception ex) {
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex, "Stat service error.");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, DateTimeParseException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ApiError> handleBadRequestException(Exception ex) {
        return getResponseEntity(HttpStatus.BAD_REQUEST, ex, "Incorrectly made request.");
    }

    private ResponseEntity<ApiError> getResponseEntity(HttpStatus status, Exception ex, String reason) {
        return new ResponseEntity<>(new ApiError(status, reason, ex), status);
    }
}
