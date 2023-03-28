package ru.practicum.ewm.stat.service.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.stat.service.model.Hit;
import ru.practicum.ewm.stat.service.tools.HitFactory;

public class HitMatcher implements ArgumentMatcher<Hit> {
    private final Hit hit;

    private HitMatcher(Hit hit) {
        this.hit = hit;
    }

    public static HitMatcher equalTo(Hit hit) {
        return new HitMatcher(hit);
    }

    @Override
    public boolean matches(Hit hit) {
        return HitFactory.equals(this.hit, hit);
    }
}