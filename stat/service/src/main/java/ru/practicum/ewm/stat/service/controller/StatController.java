package ru.practicum.ewm.stat.service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.service.service.StatService;
import java.util.List;
import static ru.practicum.ewm.common.support.DateFactory.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatController {
    StatService service;

    @PostMapping("/hit")
    public ResponseEntity<Void> create(@RequestBody EndpointHit dto) {
        service.create(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> find(@RequestParam String start,
                                                @RequestParam String end,
                                                @RequestParam String[] uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {
        List<ViewStats> listDto = service.find(dateOf(start), dateOf(end), uris, unique);

        return ResponseEntity.ok(listDto);
    }
}