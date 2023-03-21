package ru.practicum.ewm.stat.service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import ru.practicum.ewm.stat.service.service.StatService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatController {
    static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    StatService service;

    @PostMapping("/hit")
    public ResponseEntity<Void> hit(@RequestBody EndpointHitDto dto) {
        service.create(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> stats(@RequestParam String start,
                                                    @RequestParam String end,
                                                    @RequestParam String[] uris,
                                                    @RequestParam(defaultValue = "false") Boolean unique) {
        return ResponseEntity.ok(service.find(LocalDateTime.parse(start, DateTimeFormatter.ofPattern(DATE_PATTERN)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(DATE_PATTERN)), uris, unique));
    }
}