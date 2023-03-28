package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.validation.constraints.EventState;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import static ru.practicum.ewm.common.support.DateFactory.*;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService service;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> find(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(required = false) List<@EventState String> states,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) String rangeStart,
                                                   @RequestParam(required = false) String rangeEnd,
                                                   @Min(value = 0, message = "must not be less than 0")
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @Min(value = 1, message = "must not be less than 1")
                                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<EventFullDto> dtoList = service.findByAdmin(users, states, categories, dateOf(rangeStart),
                dateOf(rangeEnd), from, size);

        return ResponseEntity.ok(dtoList);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventAdminRequest inDto) {
        EventFullDto outDto = service.updateByAdmin(eventId, inDto);

        return ResponseEntity.ok(outDto);
    }
}