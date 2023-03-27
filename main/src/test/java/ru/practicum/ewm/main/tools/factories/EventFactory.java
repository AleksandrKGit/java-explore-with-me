package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.model.Location;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;

public class EventFactory {
    public static Location createLocation(Float lat, Float lon) {
        Location location = new Location();
        location.setLat(lat);
        location.setLon(lon);
        return location;
    }

    public static Event createEvent(Long id, User initiator, Category category, String title, String annotation,
                                    String description, Integer participantLimit, Location location,
                                    Boolean requestModeration, Boolean paid, LocalDateTime createdOn,
                                    LocalDateTime publishedOn, LocalDateTime eventDate, EventState state) {
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
        return event;
    }
}
