package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;

public class RequestFactory {
    public static Request createRequest(Long id, User requestor, Event event, LocalDateTime created,
                                        RequestStatus status) {
        Request request = new Request();
        request.setId(id);
        request.setRequestor(requestor);
        request.setEvent(event);
        request.setCreated(created);
        request.setStatus(status);
        return request;
    }
}
