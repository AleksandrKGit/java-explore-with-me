package ru.practicum.ewm.stat.service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.service.StatMapperImpl;
import ru.practicum.ewm.stat.service.StatRepository;
import ru.practicum.ewm.stat.service.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StatServiceImpl.class, StatMapperImpl.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class StatServiceTest {
    @Autowired
    StatService service;

    @MockBean
    StatRepository repository;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Test
    void create_shouldSaveEndpointHitEntityFromDto() {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("appName");
        endpointHit.setUri("https://ya.ru");
        endpointHit.setIp("192.168.0.1");
        endpointHit.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        ArgumentCaptor<Hit> endpointHitArgumentCaptor = ArgumentCaptor.forClass(Hit.class);
        when(repository.saveAndFlush(endpointHitArgumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0, Hit.class));

        service.create(endpointHit);

        Hit hitToRepository = endpointHitArgumentCaptor.getValue();
        assertThat(hitToRepository, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("app", equalTo(endpointHit.getApp())),
                hasProperty("uri", equalTo(endpointHit.getUri())),
                hasProperty("ip", equalTo(endpointHit.getIp())),
                hasProperty("timestamp", equalTo(LocalDateTime.parse(endpointHit.getTimestamp(),
                        DateTimeFormatter.ofPattern(DATE_PATTERN))))
        ));
    }

    final LocalDateTime start = LocalDateTime.now().minusDays(10);

    final LocalDateTime end = LocalDateTime.now();

    final String[] uris = new String[] {"https://ya.ru"};

    final List<ViewStats> viewStatsList = List.of(new ViewStats("appName", uris[0], 1L));

    @Test
    void find_withNotUniqueIp_shouldReturnRepositoryFindResult() {
        when(repository.findWithUris(start, end, uris)).thenReturn(viewStatsList);

        List<ViewStats> result = service.find(start, end, uris, false);

        assertThat(viewStatsList == result, is(true));
    }

    @Test
    void find_withUniqueIp_shouldReturnRepositoryFindUniqueResult() {
        when(repository.findUniqueWithUris(start, end, uris)).thenReturn(viewStatsList);

        List<ViewStats> result = service.find(start, end, uris, true);

        assertThat(viewStatsList == result, is(true));
    }
}