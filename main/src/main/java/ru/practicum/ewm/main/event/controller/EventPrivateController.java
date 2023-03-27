package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService service;

    @PostMapping
    public ResponseEntity<EventFullDto> create(@PathVariable Long userId,
                                               @Valid @RequestBody NewEventDto inDto) {
        EventFullDto outDto = service.create(userId, inDto);

        return new ResponseEntity<>(outDto, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> get(@PathVariable Long userId, @PathVariable Long eventId) {
        EventFullDto dto = service.getByInitiator(userId, eventId);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> find(@PathVariable Long userId,
                                                    @Min(value = 0, message = "must not be less than 0")
                                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                                    @Min(value = 1, message = "must not be less than 1")
                                                    @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<EventShortDto> dtoList = service.findByInitiator(userId, from, size);

        return ResponseEntity.ok(dtoList);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventUserRequest inDto) {
        EventFullDto outDto = service.updateByInitiator(userId, eventId, inDto);

        return ResponseEntity.ok(outDto);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findRequests(@PathVariable Long userId,
                                                                      @PathVariable Long eventId) {
        List<ParticipationRequestDto> dtoList =  service.findRequestsByInitiator(userId, eventId);

        return ResponseEntity.ok(dtoList);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequests(@PathVariable Long userId,
                                                                        @PathVariable Long eventId,
                                                                        @Valid @RequestBody
                                                                        EventRequestStatusUpdateRequest inDto) {
        EventRequestStatusUpdateResult dtoList = service.updateRequestsByInitiator(userId, eventId, inDto);

        return ResponseEntity.ok(dtoList);
    }
}