package ru.practicum.ewm.main.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String field, String error, String value) {
        super(String.format("Field: %s. Error: %s. Value: %s", field, error, value));
    }
}