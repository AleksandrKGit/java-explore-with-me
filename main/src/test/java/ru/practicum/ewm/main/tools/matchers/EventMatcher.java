package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.tools.factories.EventFactory;

public class EventMatcher implements ArgumentMatcher<Event> {
    private final Event event;

    private EventMatcher(Event event) {
        this.event = event;
    }

    public static EventMatcher equalTo(Event event) {
        return new EventMatcher(event);
    }

    @Override
    public boolean matches(Event event) {
        return EventFactory.equals(this.event, event);
    }
}