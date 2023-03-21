package ru.practicum.ewm.stat.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.service.model.EndpointHit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(classes = {StatMapperImpl.class})
class StatMapperTest {
    @Autowired
    StatMapper statMapper;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Test
    void toEntity_withNotNullFields_shouldReturnEntityWithNotNullFieldsAndNullItem() {
        EndpointHitDto source = new EndpointHitDto();
        source.setApp("appName");
        source.setUri("https://ya.ru");
        source.setIp("192.168.0.1");
        source.setTimestamp("2023-03-10 00:00:00");

        EndpointHit target = statMapper.toEntity(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("app", equalTo(source.getApp())),
                hasProperty("uri", equalTo(source.getUri())),
                hasProperty("ip", equalTo(source.getIp())),
                hasProperty("timestamp", equalTo(LocalDateTime.parse(source.getTimestamp(),
                        DateTimeFormatter.ofPattern(DATE_PATTERN))))
        ));
    }

    @Test
    void toEntity_withNull_shouldReturnNull() {
        assertThat(statMapper.toEntity(null), nullValue());
    }

    @Test
    void toEntity_withNullFields_shouldReturnEntityWithNullFields() {
        EndpointHitDto source = new EndpointHitDto();

        EndpointHit target = statMapper.toEntity(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("app", is(nullValue())),
                hasProperty("uri", is(nullValue())),
                hasProperty("ip", is(nullValue())),
                hasProperty("timestamp", is(nullValue()))
        ));
    }
}