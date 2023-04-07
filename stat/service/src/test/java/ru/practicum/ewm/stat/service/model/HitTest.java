package ru.practicum.ewm.stat.service.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.ewm.stat.service.tools.HitFactory;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

class HitTest {
    private final Long id = 1L;

    @SuppressWarnings("all")
    @Test
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue() {
        Hit hit = createHit(null, true);

        assertThat(hit.equals(hit), is(true));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse() {
        Hit hit = createHit(null, true);

        assertThat(hit.equals(null), is(false));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withObjectOfOtherClassWithNotNullIdAndNullOtherFields_shouldReturnFalse() {
        Hit hit = createHit(id, true);
        OtherObject otherObject = new OtherObject(id);

        assertThat(hit.equals(otherObject), is(false));
    }

    @Test
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse() {
        Hit hit1 = createHit(null, false);
        Hit hit2 = createHit(null, false);

        assertThat(hit1.equals(hit2), is(false));
    }

    @Test
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue() {
        Hit hit1 = createHit(id, true);
        Hit hit2 = createHit(id, false);

        assertThat(hit1.equals(hit2), is(true));
    }

    @ParameterizedTest(name = "ids={0}")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoEndpointHitsWithEqualIdsAndNotEqualOtherFields_shouldBeEqual(Long id) {
        Hit hit1 = createHit(id, false);
        Hit hit2 = createHit(id, true);

        assertThat(hit1.hashCode(), equalTo(hit2.hashCode()));
    }

    @Test
    void hashCode_ofTwoEndpointHitsWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual() {
        Hit hit1 = createHit(null, false);
        Hit hit2 = createHit(0L, true);

        assertThat(hit1.hashCode(), equalTo(hit2.hashCode()));
    }

    @ParameterizedTest(name = "id1={0}, id2=2")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoEndpointHitsWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(Long id) {
        Hit hit1 = createHit(id, false);
        Hit hit2 = createHit(2L, false);

        assertThat(hit1.hashCode(), not(equalTo(hit2.hashCode())));
    }

    private Hit createHit(Long id, boolean nullOtherFields) {
        return nullOtherFields ? HitFactory.createHit(id, null, null, null, null)
                : HitFactory.createHit(id, "appName", "https://ya.ru", "192.168.0.1", LocalDateTime.now());
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