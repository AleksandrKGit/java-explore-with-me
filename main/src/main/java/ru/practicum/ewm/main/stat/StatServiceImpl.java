package ru.practicum.ewm.main.stat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.exception.StatServiceException;
import ru.practicum.ewm.stat.client.StatClient;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import static ru.practicum.ewm.common.support.DateFactory.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatServiceImpl implements StatService {
    StatClient statClient;

    ObjectMapper objectMapper;

    private static final String app = "ewm-main-service";

    private String getEventUri(Long id) {
        return "/events/" + id;
    }

    private List<ViewStats> getViewStats(List<Long> ids) {
        try {
            ResponseEntity<String> response = statClient.stats(LocalDateTime.now().minusYears(100),
                    LocalDateTime.now(), ids.stream().map(this::getEventUri).toArray(String[]::new), false);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception(response.getStatusCodeValue() + ": " + response.getBody());
            }

            return objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, ViewStats.class));
        } catch (Exception exception) {
            throw new StatServiceException(exception.getMessage());
        }
    }

    @Override
    public List<Pair<Long, Long>> getViewsSortedByCount(List<Long> ids) {
        ids = new LinkedList<>(ids);

        List<ViewStats> dtoList = getViewStats(ids);

        List<Pair<Long, Long>> views = dtoList.stream()
                .map(dto -> Pair.of(Long.valueOf(dto.getUri().substring(8)), dto.getHits()))
                .collect(Collectors.toCollection(LinkedList::new));

        ids.removeAll(views.stream().map(Pair::getFirst).collect(Collectors.toList()));
        ids.forEach(id -> views.add(Pair.of(id, 0L)));

        return views;
    }

    @Override
    public Map<Long, Long> getViews(List<Long> ids) {
        List<ViewStats> dtoList = getViewStats(ids);

        Map<Long, Long> views = new TreeMap<>(Long::compareTo);
        dtoList.forEach(dto -> views.put(Long.valueOf(dto.getUri().substring(8)), dto.getHits()));
        return views;
    }

    private void hit(String ip, String uri) {
        EndpointHit dto = new EndpointHit();
        dto.setTimestamp(ofDate(LocalDateTime.now()));
        dto.setUri(uri);
        dto.setIp(ip);
        dto.setApp(app);

        try {
            ResponseEntity<String> response = statClient.hit(dto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception(response.getStatusCodeValue() + ": " + response.getBody());
            }
        } catch (Exception exception) {
            throw new StatServiceException(exception.getMessage());
        }
    }

    @Override
    public void addFindEventsEndPoint(String ip, String uri) {
        hit(ip, uri);
    }

    @Override
    public void addEventEndPoint(String ip, Long id) {
        hit(ip, getEventUri(id));
    }
}