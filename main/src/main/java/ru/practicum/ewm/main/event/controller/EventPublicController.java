package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.service.EventService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;
import static ru.practicum.ewm.common.support.DateFactory.*;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService service;

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> get(@PathVariable Long id, HttpServletRequest request) {
        EventFullDto dto = service.get(request.getRemoteAddr(), id);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> find(@RequestParam(required = false) String text,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false) Boolean paid,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                    @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                    @Min(value = 0, message = "must not be less than 0")
                                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                                    @Min(value = 1, message = "must not be less than 1")
                                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                                    HttpServletRequest request) {
        List<EventShortDto> dtoList = service.find(request.getRemoteAddr(), text, categories, paid, dateOf(rangeStart),
                dateOf(rangeEnd), onlyAvailable, EventSort.valueOf(sort), from, size);

        return ResponseEntity.ok(dtoList);
    }
}