package ru.practicum.ewm.stat.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import ru.practicum.ewm.stat.service.service.StatService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatController {
    static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    StatService service;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void handleException(Exception ignored) {
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, DateTimeParseException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleBadRequestException(Exception ignored) {
    }

    @PostMapping("/hit")
    public HttpStatus hit(@RequestBody EndpointHitDto dto) {
        service.create(dto);
        return HttpStatus.CREATED;
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> stats(@RequestParam String start, @RequestParam String end, @RequestParam String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        return service.find(LocalDateTime.parse(start, DateTimeFormatter.ofPattern(DATE_PATTERN)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(DATE_PATTERN)), uris, unique);
    }
}