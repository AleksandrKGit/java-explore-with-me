package ru.practicum.ewm.main.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.RequestRepository;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.dto.RequestMapper;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;
import java.util.List;
import java.util.Set;
import static ru.practicum.ewm.common.support.DateFactory.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepository repository;

    UserRepository userRepository;

    EventRepository eventRepository;

    RequestMapper mapper;

    @Transactional
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new NotFoundException(String.format("User with id = %s was not found", userId));
        }

        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            throw new NotFoundException(String.format("Event with id = %s was not found", eventId));
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Request by initiator cannot be added");
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException(String.format("Event with id = %s has not been published yet", eventId));
        }

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ConflictException(String.format("Event with id = %s has reached participants limit", eventId));
        }

        Request request = new Request();
        request.setRequestor(user);
        request.setEvent(event);
        request.setStatus(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED);
        request.setCreated(now());

        request = repository.saveAndFlush(request);

        if (RequestStatus.CONFIRMED.equals(request.getStatus())) {
            eventRepository.increaseConfirmedEvents(eventId, 1L);
        }

        return mapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> find(Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new NotFoundException(String.format("User with id = %s was not found", userId));
        }

        List<Request> requests = repository.findByRequestor_Id(userId);

        return mapper.toDto(requests);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancel(Long userId, Long id) {
        Request request = repository.findById(id).orElse(null);

        if (request == null) {
            throw new NotFoundException(String.format("Request with id = %s was not found", id));
        }

        if (RequestStatus.REJECTED.equals(request.getStatus()) || RequestStatus.CANCELED.equals(request.getStatus())) {
            throw new ConflictException(String.format("Request with id = %s was rejected or canceled", id));
        }

        boolean decrease = RequestStatus.CONFIRMED.equals(request.getStatus());
        repository.updateStatus(Set.of(id), RequestStatus.CANCELED);
        request.setStatus(RequestStatus.CANCELED);

        if (decrease) {
            eventRepository.decrementConfirmedEvents(request.getEvent().getId());
        }

        return mapper.toDto(request);
    }
}