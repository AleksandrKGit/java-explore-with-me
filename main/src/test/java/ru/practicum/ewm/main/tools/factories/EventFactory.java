package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.compilation.Compilation;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.model.Location;
import ru.practicum.ewm.main.tools.matchers.DateMatcher;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class EventFactory {
    public static Location createLocation(Float lat, Float lon) {
        Location location = new Location();

        location.setLat(lat);
        location.setLon(lon);

        return location;
    }

    public static Event createEvent(Long id, User initiator, Category category, String title, String annotation,
                                    String description, Location location, Boolean paid, Boolean requestModeration,
                                    Integer participantLimit, LocalDateTime createdOn, LocalDateTime publishedOn,
                                    LocalDateTime eventDate, EventState state, Long confirmedRequests,
                                    List<Compilation> compilations) {
        Event event = new Event();

        event.setId(id);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setTitle(title);
        event.setAnnotation(annotation);
        event.setDescription(description);
        event.setParticipantLimit(participantLimit);
        event.setLocation(location);
        event.setRequestModeration(requestModeration);
        event.setPaid(paid);
        event.setCreatedOn(createdOn);
        event.setPublishedOn(publishedOn);
        event.setEventDate(eventDate);
        event.setState(state);
        event.setConfirmedRequests(confirmedRequests);
        event.setCompilations(compilations);

        return event;
    }

    public static EventFullDto createEventFullDto(Long id, UserShortDto initiator, CategoryDto category, String title,
                                                  String annotation, String description, Location location,
                                                  Boolean paid, Boolean requestModeration, Integer participantLimit,
                                                  String createdOn, String publishedOn, String eventDate, String state,
                                                  Long confirmedRequests, Long views) {
        EventFullDto dto = new EventFullDto();

        dto.setId(id);
        dto.setInitiator(initiator);
        dto.setCategory(category);
        dto.setTitle(title);
        dto.setAnnotation(annotation);
        dto.setDescription(description);
        dto.setLocation(location);
        dto.setPaid(paid);
        dto.setRequestModeration(requestModeration);
        dto.setParticipantLimit(participantLimit);
        dto.setCreatedOn(createdOn);
        dto.setPublishedOn(publishedOn);
        dto.setEventDate(eventDate);
        dto.setState(state);
        dto.setConfirmedRequests(confirmedRequests);
        dto.setViews(views);

        return dto;
    }

    public static EventShortDto createEventShortDto(Long id, UserShortDto initiator, CategoryDto category, String title,
                                                    String annotation, Boolean paid, String eventDate,
                                                    Long confirmedRequests, Long views) {
        EventShortDto dto = new EventShortDto();

        dto.setId(id);
        dto.setInitiator(initiator);
        dto.setCategory(category);
        dto.setTitle(title);
        dto.setAnnotation(annotation);
        dto.setPaid(paid);
        dto.setEventDate(eventDate);
        dto.setConfirmedRequests(confirmedRequests);
        dto.setViews(views);

        return dto;
    }

    public static NewEventDto createNewEventDto(Long category, String title, String annotation, String description,
                                                Location location, Boolean paid, Boolean requestModeration,
                                                Integer participantLimit, String eventDate) {
        NewEventDto dto = new NewEventDto();

        dto.setCategory(category);
        dto.setTitle(title);
        dto.setAnnotation(annotation);
        dto.setDescription(description);
        dto.setLocation(location);
        dto.setPaid(paid);
        dto.setRequestModeration(requestModeration);
        dto.setParticipantLimit(participantLimit);
        dto.setEventDate(eventDate);

        return dto;
    }

    public static UpdateEventUserRequest createUpdateEventUserRequest(Long category, String title, String annotation,
                                                                      String description, Location location,
                                                                      Boolean paid, Boolean requestModeration,
                                                                      Integer participantLimit, String eventDate,
                                                                      String stateAction) {
        UpdateEventUserRequest dto = new UpdateEventUserRequest();

        dto.setCategory(category);
        dto.setTitle(title);
        dto.setAnnotation(annotation);
        dto.setDescription(description);
        dto.setLocation(location);
        dto.setPaid(paid);
        dto.setRequestModeration(requestModeration);
        dto.setParticipantLimit(participantLimit);
        dto.setEventDate(eventDate);
        dto.setStateAction(stateAction);

        return dto;
    }

    public static UpdateEventAdminRequest createUpdateEventAdminRequest(Long category, String title, String annotation,
                                                                        String description, Location location,
                                                                        Boolean paid, Boolean requestModeration,
                                                                        Integer participantLimit, String eventDate,
                                                                        String stateAction) {
        UpdateEventAdminRequest dto = new UpdateEventAdminRequest();

        dto.setCategory(category);
        dto.setTitle(title);
        dto.setAnnotation(annotation);
        dto.setDescription(description);
        dto.setLocation(location);
        dto.setPaid(paid);
        dto.setRequestModeration(requestModeration);
        dto.setParticipantLimit(participantLimit);
        dto.setEventDate(eventDate);
        dto.setStateAction(stateAction);

        return dto;
    }

    public static Event copyOf(Event event) {
        if (event == null) {
            return null;
        }

        Event copy = new Event();
        event.setId(event.getId());
        event.setInitiator(event.getInitiator());
        event.setCategory(event.getCategory());
        event.setTitle(event.getTitle());
        event.setAnnotation(event.getAnnotation());
        event.setDescription(event.getDescription());
        event.setParticipantLimit(event.getParticipantLimit());
        event.setLocation(event.getLocation());
        event.setRequestModeration(event.getRequestModeration());
        event.setPaid(event.getPaid());
        event.setCreatedOn(event.getCreatedOn());
        event.setPublishedOn(event.getPublishedOn());
        event.setEventDate(event.getEventDate());
        event.setState(event.getState());
        event.setConfirmedRequests(event.getConfirmedRequests());
        event.setCompilations(event.getCompilations());

        return copy;
    }

    public static boolean equals(Location location1, Location location2) {
        if (location1 == null && location2 == null) {
            return true;
        }

        return location1 != null && location2 != null
                && Objects.equals(location1.getLat(), location2.getLat())
                && Objects.equals(location1.getLon(), location2.getLon());
    }


    public static boolean equals(Event event1, Event event2) {
        if (event1 == null && event2 == null) {
            return true;
        }

        return event1 != null && event2 != null
                && Objects.equals(event1.getId(), event2.getId())
                && Objects.equals(event1.getInitiator(), event2.getInitiator())
                && Objects.equals(event1.getCategory(), event2.getCategory())
                && Objects.equals(event1.getTitle(), event2.getTitle())
                && Objects.equals(event1.getAnnotation(), event2.getAnnotation())
                && Objects.equals(event1.getDescription(), event2.getDescription())
                && equals(event1.getLocation(), event2.getLocation())
                && Objects.equals(event1.getPaid(), event2.getPaid())
                && Objects.equals(event1.getRequestModeration(), event2.getRequestModeration())
                && Objects.equals(event1.getParticipantLimit(), event2.getParticipantLimit())
                && DateMatcher.near(event1.getCreatedOn(), event2.getCreatedOn())
                && DateMatcher.near(event1.getPublishedOn(), event2.getPublishedOn())
                && Objects.equals(event1.getEventDate(), event2.getEventDate())
                && Objects.equals(event1.getState(), event2.getState())
                && Objects.equals(event1.getConfirmedRequests(), event2.getConfirmedRequests())
                && Objects.equals(event1.getCompilations(), event2.getCompilations());
    }

    public static boolean equals(EventFullDto dto1, EventFullDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getId(), dto2.getId())
                && UserFactory.equals(dto1.getInitiator(), dto2.getInitiator())
                && CategoryFactory.equals(dto1.getCategory(), dto2.getCategory())
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getAnnotation(), dto2.getAnnotation())
                && Objects.equals(dto1.getDescription(), dto2.getDescription())
                && equals(dto1.getLocation(), dto2.getLocation())
                && Objects.equals(dto1.getPaid(), dto2.getPaid())
                && Objects.equals(dto1.getRequestModeration(), dto2.getRequestModeration())
                && Objects.equals(dto1.getParticipantLimit(), dto2.getParticipantLimit())
                && DateMatcher.near(dto1.getCreatedOn(), dto2.getCreatedOn())
                && DateMatcher.near(dto1.getPublishedOn(), dto2.getPublishedOn())
                && Objects.equals(dto1.getEventDate(), dto2.getEventDate())
                && Objects.equals(dto1.getState(), dto2.getState())
                && Objects.equals(dto1.getConfirmedRequests(), dto2.getConfirmedRequests())
                && Objects.equals(dto1.getViews(), dto2.getViews());
    }

    public static boolean equals(EventShortDto dto1, EventShortDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getId(), dto2.getId())
                && UserFactory.equals(dto1.getInitiator(), dto2.getInitiator())
                && CategoryFactory.equals(dto1.getCategory(), dto2.getCategory())
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getAnnotation(), dto2.getAnnotation())
                && Objects.equals(dto1.getPaid(), dto2.getPaid())
                && Objects.equals(dto1.getEventDate(), dto2.getEventDate())
                && Objects.equals(dto1.getConfirmedRequests(), dto2.getConfirmedRequests())
                && Objects.equals(dto1.getViews(), dto2.getViews());
    }

    public static boolean equals(List<EventShortDto> dtoList1, List<EventShortDto> dtoList2) {
        if (dtoList1 == null && dtoList2 == null) {
            return true;
        }

        if (dtoList1 == null || dtoList2 == null || dtoList1.size() != dtoList2.size()) {
            return false;
        }

        for (int i = 0; i < dtoList1.size(); i++) {
            if (!equals(dtoList1.get(i), dtoList2.get(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(NewEventDto dto1, NewEventDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getCategory(), dto2.getCategory())
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getAnnotation(), dto2.getAnnotation())
                && Objects.equals(dto1.getDescription(), dto2.getDescription())
                && equals(dto1.getLocation(), dto2.getLocation())
                && Objects.equals(dto1.getPaid(), dto2.getPaid())
                && Objects.equals(dto1.getRequestModeration(), dto2.getRequestModeration())
                && Objects.equals(dto1.getParticipantLimit(), dto2.getParticipantLimit())
                && Objects.equals(dto1.getEventDate(), dto2.getEventDate());
    }

    public static boolean equals(UpdateEventUserRequest dto1, UpdateEventUserRequest dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getCategory(), dto2.getCategory())
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getAnnotation(), dto2.getAnnotation())
                && Objects.equals(dto1.getDescription(), dto2.getDescription())
                && equals(dto1.getLocation(), dto2.getLocation())
                && Objects.equals(dto1.getPaid(), dto2.getPaid())
                && Objects.equals(dto1.getRequestModeration(), dto2.getRequestModeration())
                && Objects.equals(dto1.getParticipantLimit(), dto2.getParticipantLimit())
                && Objects.equals(dto1.getEventDate(), dto2.getEventDate())
                && Objects.equals(dto1.getStateAction(), dto2.getStateAction());
    }

    public static boolean equals(UpdateEventAdminRequest dto1, UpdateEventAdminRequest dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getCategory(), dto2.getCategory())
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getAnnotation(), dto2.getAnnotation())
                && Objects.equals(dto1.getDescription(), dto2.getDescription())
                && equals(dto1.getLocation(), dto2.getLocation())
                && Objects.equals(dto1.getPaid(), dto2.getPaid())
                && Objects.equals(dto1.getRequestModeration(), dto2.getRequestModeration())
                && Objects.equals(dto1.getParticipantLimit(), dto2.getParticipantLimit())
                && Objects.equals(dto1.getEventDate(), dto2.getEventDate())
                && Objects.equals(dto1.getStateAction(), dto2.getStateAction());
    }
}