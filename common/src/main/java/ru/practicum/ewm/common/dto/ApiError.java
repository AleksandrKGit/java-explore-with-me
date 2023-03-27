package ru.practicum.ewm.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static ru.practicum.ewm.common.support.DateFactory.*;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {
    List<String> errors;

    HttpStatus status;

    String reason;

    String message;

    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime timestamp;

    public ApiError(HttpStatus status, String reason, Exception exception) {
        this.status = status;
        this.reason = reason;
        this.message = exception == null ? null : exception.getMessage();
        this.timestamp = LocalDateTime.now();
        this.errors = exception == null ? null : Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }

    public ApiError(HttpStatus status, String reason, List<String> errors) {
        this.status = status;
        this.reason = reason;
        this.message = errors == null ? null : String.join("\r\n", errors);
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}