package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.tools.factories.RequestFactory;

public class RequestMatcher implements ArgumentMatcher<Request> {
    private final Request request;

    private RequestMatcher(Request request) {
        this.request = request;
    }

    public static RequestMatcher equalTo(Request request) {
        return new RequestMatcher(request);
    }

    @Override
    public boolean matches(Request request) {
        return RequestFactory.equals(this.request, request);
    }
}