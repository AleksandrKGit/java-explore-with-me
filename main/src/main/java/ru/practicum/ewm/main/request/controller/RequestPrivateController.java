package ru.practicum.ewm.main.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.service.RequestService;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestService service;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> create(@PathVariable Long userId,
                                                          @RequestParam Long eventId) {
        ParticipationRequestDto dto = service.create(userId, eventId);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> find(@PathVariable Long userId) {
        List<ParticipationRequestDto> dtoList = service.find(userId);

        return ResponseEntity.ok(dtoList);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancel(@PathVariable Long userId,
                                                          @PathVariable Long requestId) {
        ParticipationRequestDto dto = service.cancel(userId, requestId);

        return ResponseEntity.ok(dto);
    }
}