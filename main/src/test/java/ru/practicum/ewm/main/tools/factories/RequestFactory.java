package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.tools.matchers.DateMatcher;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    public static ParticipationRequestDto createParticipationRequestDto(Long id, Long requester, Long event,
                                                                        String created, String status) {
        ParticipationRequestDto dto = new ParticipationRequestDto();

        dto.setId(id);
        dto.setRequester(requester);
        dto.setEvent(event);
        dto.setCreated(created);
        dto.setStatus(status);

        return dto;
    }

    public static EventRequestStatusUpdateRequest createEventRequestStatusUpdateRequest(String status,
                                                                                        List<Long> requestIds) {
        EventRequestStatusUpdateRequest dto = new EventRequestStatusUpdateRequest();

        dto.setStatus(status);
        dto.setRequestIds(requestIds);

        return dto;
    }

    public static EventRequestStatusUpdateResult createEventRequestStatusUpdateResult(
            List<ParticipationRequestDto> confirmedRequests, List<ParticipationRequestDto> rejectedRequests) {
        EventRequestStatusUpdateResult dto = new EventRequestStatusUpdateResult();

        dto.setConfirmedRequests(confirmedRequests);
        dto.setRejectedRequests(rejectedRequests);

        return dto;
    }

    public static Request copyOf(Request request) {
        if (request == null) {
            return request;
        }

        Request copy = new Request();

        copy.setId(request.getId());
        copy.setRequestor(request.getRequestor());
        copy.setEvent(request.getEvent());
        copy.setCreated(request.getCreated());
        copy.setStatus(request.getStatus());

        return copy;
    }

    public static boolean equals(Request request1, Request request2) {
        if (request1 == null && request2 == null) {
            return true;
        }

        return request1 != null && request2 != null
                && Objects.equals(request1.getId(), request2.getId())
                && Objects.equals(request1.getRequestor(), request2.getRequestor())
                && Objects.equals(request1.getEvent(), request2.getEvent())
                && DateMatcher.near(request1.getCreated(), request2.getCreated())
                && Objects.equals(request1.getStatus(), request2.getStatus());
    }

    public static boolean equals(ParticipationRequestDto dto1, ParticipationRequestDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getId(), dto2.getId())
                && Objects.equals(dto1.getRequester(), dto2.getRequester())
                && Objects.equals(dto1.getEvent(), dto2.getEvent())
                && DateMatcher.near(dto1.getCreated(), dto2.getCreated())
                && Objects.equals(dto1.getStatus(), dto2.getStatus());
    }

    public static boolean equals(List<ParticipationRequestDto> dtoList1, List<ParticipationRequestDto> dtoList2) {
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

    public static boolean equals(EventRequestStatusUpdateRequest dto1, EventRequestStatusUpdateRequest dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getStatus(), dto2.getStatus())
                && Objects.equals(dto1.getRequestIds(), dto2.getRequestIds());
    }

    public static boolean equals(EventRequestStatusUpdateResult dto1, EventRequestStatusUpdateResult dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && equals(dto1.getConfirmedRequests(), dto2.getConfirmedRequests())
                && equals(dto1.getRejectedRequests(), dto2.getRejectedRequests());
    }
}
