package ru.practicum.ewm.stat.client;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class QueryParameters {
    final Map<String, Object> parameters;

    public Map<String, Object> getParameters() {
        return parameters.size() == 0 ? null : parameters;
    }

    String query;

    public String getQuery() {
        return query.isEmpty() ? "" : "?" + query;
    }

    public QueryParameters() {
        parameters = new HashMap<>();
        query = "";
    }

    public void add(String name, @Nullable Object value) {
        if (name == null) {
            throw new NullPointerException("Query parameter name should not be null");
        }

        if (value != null) {
            parameters.put(name, value);
            query = (query.isEmpty() ? "" : query + "&") + name + "={" + name + "}";
        }
    }
}