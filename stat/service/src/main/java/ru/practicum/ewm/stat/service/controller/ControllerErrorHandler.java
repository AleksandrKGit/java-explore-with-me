package ru.practicum.ewm.stat.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Void> handleException(Exception ignored) {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, DateTimeParseException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<Void> handleBadRequestException(Exception ignored) {
        return ResponseEntity.badRequest().build();
    }
}
