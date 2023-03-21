package ru.practicum.ewm.stat.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.common.dto.ExceptionDto;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ExceptionDto(status, "Stat service error.", exception.getMessage()),
                status);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, DateTimeParseException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ExceptionDto> handleBadRequestException(Exception exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ExceptionDto(status, "Incorrectly made request.",
                exception.getMessage()), status);
    }
}
