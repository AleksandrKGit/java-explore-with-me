package ru.practicum.ewm.main.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.CategoryRepository;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.event.controller.EventSort;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.exception.BadRequestException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.request.RequestRepository;
import ru.practicum.ewm.main.request.dto.RequestMapper;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.exception.ForbiddenException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.stat.StatService;
import ru.practicum.ewm.main.support.OffsetPageRequest;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static ru.practicum.ewm.common.support.DateFactory.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    EventMapper mapper;

    RequestMapper requestMapper;

    EventRepository repository;

    CategoryRepository categoryRepository;

    UserRepository userRepository;

    RequestRepository requestRepository;

    StatService statService;

    private void checkEventDate(LocalDateTime eventDate, Integer hours) {
        if (eventDate.isBefore(now().plusHours(hours))) {
            throw new ForbiddenException("eventDate", "Must not be earlier than " + hours + " h. after now",
                    ofDate(eventDate));
        }
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto dto) {
        checkEventDate(dateOf(dto.getEventDate()), 2);

        User initiator = userRepository.findById(userId).orElse(null);

        if (initiator == null) {
            throw new NotFoundException("User with id = " + userId + " was not found");
        }

        Category category = categoryRepository.findById(dto.getCategory()).orElse(null);

        if (category == null) {
            throw new NotFoundException("Category with id = " + dto.getCategory() + " was not found");
        }

        Event event = mapper.toEntity(dto, initiator, category, now(), EventState.PENDING);

        return mapper.toFullDto(repository.saveAndFlush(event), 0L);
    }

    @Override
    public EventFullDto get(String ip, Long id) {
        Event event = repository.getByState(id, EventState.PUBLISHED).orElse(null);

        if (event == null) {
            throw new NotFoundException("Event with id = " + id + " was not found");
        }

        statService.addEventEndPoint(ip, id);

        return mapper.toFullDto(event);
    }

    @Override
    public EventFullDto getByInitiator(Long userId, Long id) {
        Event event = repository.get(id).orElse(null);

        if (event == null || !Objects.equals(userId, event.getInitiator().getId())) {
            throw new NotFoundException("Event with id=" + id + " was not found");
        }

        return mapper.toFullDto(event);
    }

    @Override
    public List<EventShortDto> find(String ip, String uri, String text, List<Long> categoryIds, Boolean paid,
                                    LocalDateTime start, LocalDateTime end, Boolean onlyAvailable, EventSort sort,
                                    Integer from, Integer size) {
        statService.addFindEventsEndPoint(ip, uri);

        List<Long> ids = repository.findByQuery(text, categoryIds, paid, start, end, onlyAvailable, from, size, sort);

        if (ids.size() == 0) {
            return List.of();
        }

        List<Event> events = repository.get(ids);

        // Saving query sort
        if (sort.equals(EventSort.EVENT_DATE)) {
            Map<Long, Event> eventsMap = new TreeMap<>(Long::compareTo);
            events.forEach(e -> eventsMap.put(e.getId(), e));
            events = ids.stream().map(eventsMap::get).collect(Collectors.toList());
        }

        return mapper.toShortDto(events, sort.equals(EventSort.VIEWS));
    }

    @Override
    public List<EventShortDto> findByInitiator(Long userId, Integer from, Integer size) {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, null);

        List<Long> ids = repository.findByInitiator(userId, pageRequest);

        if (ids.size() == 0) {
            return List.of();
        }

        return mapper.toShortDto(repository.get(ids), false);
    }

    @Override
    public List<EventFullDto> findByAdmin(List<Long> userIds, List<String> states, List<Long> categoryIds,
                                          LocalDateTime start, LocalDateTime end, Integer from, Integer size) {
        List<Long> ids = repository.findByAdmin(userIds, states == null ? null : states.stream()
                .map(EventState::valueOf).collect(Collectors.toList()), categoryIds, start, end, from, size);

        if (ids.size() == 0) {
            return List.of();
        }

        return mapper.toFullDto(repository.get(ids), false);
    }

    private void updateCategory(Event event, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);

            if (category == null) {
                throw new NotFoundException("Category with id = " + categoryId + " was not found");
            }

            event.setCategory(category);
        }
    }

    @Override
    public EventFullDto updateByInitiator(Long userId, Long id, UpdateEventUserRequest dto) {
        if (dto.getEventDate() != null) {
            checkEventDate(dateOf(dto.getEventDate()), 2);
        }

        Event event = repository.get(id).orElse(null);

        if (event == null || !Objects.equals(userId, event.getInitiator().getId())) {
            throw new NotFoundException("Event with id = " + id + " was not found");
        }

        if (dto.getEventDate() == null) {
            checkEventDate(event.getEventDate(), 2);
        }

        if (!event.getState().equals(EventState.PENDING) && !event.getState().equals(EventState.CANCELED)) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        updateCategory(event, dto.getCategory());

        EventState state = dto.getStateAction() == null ? null :
                (EventUserStateAction.valueOf(dto.getStateAction()).equals(EventUserStateAction.CANCEL_REVIEW) ?
                        EventState.CANCELED : EventState.PENDING);

        mapper.updateFromUser(dto, event, state);

        return mapper.toFullDto(repository.saveAndFlush(event), 0L);
    }

    @Override
    public EventFullDto updateByAdmin(Long id, UpdateEventAdminRequest dto) {
        if (dto.getEventDate() != null) {
            checkEventDate(dateOf(dto.getEventDate()), 2);
        }

        Event event = repository.get(id).orElse(null);

        if (event == null) {
            throw new NotFoundException("Event with id = " + id + " was not found");
        }

        EventState state = null;
        LocalDateTime publishedOn = null;

        if (dto.getStateAction() != null) {
            boolean doPublish = dto.getStateAction().equals(EventAdminStateAction.PUBLISH_EVENT.toString());

            if (doPublish && !event.getState().equals(EventState.PENDING)) {
                throw new ForbiddenException("Only pending events can be published");
            }

            if (!doPublish && event.getState().equals(EventState.PUBLISHED)) {
                throw new ForbiddenException("Event already published");
            }

            state = doPublish ? EventState.PUBLISHED : EventState.CANCELED;

            LocalDateTime eventDate = dto.getEventDate() == null ? event.getEventDate() : dateOf(dto.getEventDate());

            if (doPublish) {
                checkEventDate(eventDate, 1);

                publishedOn = now();
            }
        }

        updateCategory(event, dto.getCategory());

        mapper.updateFromAdmin(dto, event, state, publishedOn);

        return mapper.toFullDto(repository.saveAndFlush(event), 0L);
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByInitiator(Long userId, Long eventId) {
        List<Request> requests = requestRepository.findByEventInitiator(userId, eventId);

        return requestMapper.toDto(requests);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsByInitiator(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest dto) {
        Event event = repository.findById(eventId).orElse(null);

        if (event == null || !Objects.equals(userId, event.getInitiator().getId())) {
            throw new NotFoundException("Event with id = " + eventId + " was not found");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event must be published");
        }

        if (!event.getRequestModeration()) {
            throw new ConflictException("Event does not need request moderation");
        }

        RequestStatus status = dto.getStatus().equals(RequestUpdateStatusAction.CONFIRMED.toString()) ?
                RequestStatus.CONFIRMED : RequestStatus.REJECTED;

        if (status.equals(RequestStatus.CONFIRMED) && event.getParticipantLimit() != 0
                && ((event.getConfirmedRequests() + dto.getRequestIds().size()) > event.getParticipantLimit())) {
            throw new ConflictException("The participant limit has been reached");
        }

        Set<Long> ids = new HashSet<>(dto.getRequestIds());
        List<Request> requests = requestRepository.findByIdInAndEvent_Id(ids, eventId);

        if (ids.size() != requests.size()) {
            throw new NotFoundException("Not all requests were found");
        }

        requests.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Requests must have status PENDING");
            }

            request.setStatus(status);
        });

        requestRepository.updateStatus(ids, status);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        if (status.equals(RequestStatus.CONFIRMED)) {
            repository.increaseConfirmedEvents(event.getId(), (long) ids.size());

            result.setConfirmedRequests(requestMapper.toDto(requests));
        } else {
            result.setRejectedRequests(requestMapper.toDto(requests));
        }

        return result;
    }
}