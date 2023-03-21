package ru.practicum.ewm.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExceptionDto {
    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    HttpStatus status;

    String reason;

    String message;

    @JsonFormat(pattern = DATE_PATTERN)
    LocalDateTime timestamp;

    public ExceptionDto(HttpStatus status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}