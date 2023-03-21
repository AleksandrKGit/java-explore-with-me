package ru.practicum.ewm.stat.service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import ru.practicum.ewm.stat.service.StatMapperImpl;
import ru.practicum.ewm.stat.service.StatRepository;
import ru.practicum.ewm.stat.service.model.EndpointHit;
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
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("appName");
        endpointHitDto.setUri("https://ya.ru");
        endpointHitDto.setIp("192.168.0.1");
        endpointHitDto.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        ArgumentCaptor<EndpointHit> endpointHitArgumentCaptor = ArgumentCaptor.forClass(EndpointHit.class);
        when(repository.saveAndFlush(endpointHitArgumentCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0, EndpointHit.class));

        service.create(endpointHitDto);

        EndpointHit endpointHitToRepository = endpointHitArgumentCaptor.getValue();
        assertThat(endpointHitToRepository, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("app", equalTo(endpointHitDto.getApp())),
                hasProperty("uri", equalTo(endpointHitDto.getUri())),
                hasProperty("ip", equalTo(endpointHitDto.getIp())),
                hasProperty("timestamp", equalTo(LocalDateTime.parse(endpointHitDto.getTimestamp(),
                        DateTimeFormatter.ofPattern(DATE_PATTERN))))
        ));
    }

    final LocalDateTime start = LocalDateTime.now().minusDays(10);

    final LocalDateTime end = LocalDateTime.now();

    final String[] uris = new String[] {"https://ya.ru"};

    final List<ViewStatsDto> viewStatsDtoList = List.of(new ViewStatsDto("appName", uris[0], 1L));

    @Test
    void find_withNotUniqueIp_shouldReturnRepositoryFindResult() {
        when(repository.find(start, end, uris)).thenReturn(viewStatsDtoList);

        List<ViewStatsDto> result = service.find(start, end, uris, false);

        assertThat(viewStatsDtoList == result, is(true));
    }

    @Test
    void find_withUniqueIp_shouldReturnRepositoryFindUniqueResult() {
        when(repository.findUnique(start, end, uris)).thenReturn(viewStatsDtoList);

        List<ViewStatsDto> result = service.find(start, end, uris, true);

        assertThat(viewStatsDtoList == result, is(true));
    }
}