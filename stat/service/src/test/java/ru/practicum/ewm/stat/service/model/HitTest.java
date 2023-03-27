package ru.practicum.ewm.stat.service.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class HitTest {
    Long id = 1L;
    String app = "appName";
    String uri = "https://ya.ru";
    String ip = "192.168.0.1";
    LocalDateTime timestamp = LocalDateTime.now();

    @SuppressWarnings("all")
    @Test
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue() {
        Hit hit = createEndpointHit(null, null, null, null, null);

        assertThat(hit.equals(hit), is(true));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse() {
        Hit hit = createEndpointHit(null, null, null, null, null);

        assertThat(hit.equals(null), is(false));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withObjectOfOtherClassWithNotNullIdAndNullOtherFields_shouldReturnFalse() {
        Hit hit = createEndpointHit(id, null, null, null, null);
        OtherObject otherObject = new OtherObject(id);

        assertThat(hit.equals(otherObject), is(false));
    }

    @Test
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse() {
        Hit hit1 = createEndpointHit(null, app, uri, ip, timestamp);
        Hit hit2 = createEndpointHit(null, app, uri, ip, timestamp);

        assertThat(hit1.equals(hit2), is(false));
    }

    @Test
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue() {
        Hit hit1 = createEndpointHit(id, null, null, null, null);
        Hit hit2 = createEndpointHit(id, app, uri, ip, timestamp);

        assertThat(hit1.equals(hit2), is(true));
    }

    @ParameterizedTest(name = "ids={0}")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoEndpointHitsWithEqualIdsAndNotEqualOtherFields_shouldBeEqual(Long id) {
        Hit hit1 = createEndpointHit(id, app, uri, ip, timestamp);
        Hit hit2 = createEndpointHit(id, null, null, null, null);

        assertThat(hit1.hashCode(), equalTo(hit2.hashCode()));
    }

    @Test
    void hashCode_ofTwoEndpointHitsWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual() {
        Hit hit1 = createEndpointHit(null, app, uri, ip, timestamp);
        Hit hit2 = createEndpointHit(0L, null, null, null, null);

        assertThat(hit1.hashCode(), equalTo(hit2.hashCode()));
    }

    @ParameterizedTest(name = "id1={0}, id2=2")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoEndpointHitsWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(Long id) {
        Hit hit1 = createEndpointHit(id, app, uri, ip, timestamp);
        Hit hit2 = createEndpointHit(2L, app, uri, ip, timestamp);

        assertThat(hit1.hashCode(), not(equalTo(hit2.hashCode())));
    }

    Hit createEndpointHit(Long id, String app, String uri, String ip, LocalDateTime timestamp) {
        Hit hit = new Hit();
        hit.setId(id);
        hit.setApp(app);
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(timestamp);
        return hit;
    }

    @SuppressWarnings("all")
    static class OtherObject {
        private Long id;

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public OtherObject(Long id) {
            this.id = id;
        }
    }
}