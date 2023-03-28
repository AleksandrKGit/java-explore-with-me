package ru.practicum.ewm.stat.service.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.service.tools.HitFactory;

public class ViewStatsMatcher implements ArgumentMatcher<ViewStats> {
    private final ViewStats dto;

    private ViewStatsMatcher(ViewStats dto) {
        this.dto = dto;
    }

    public static ViewStatsMatcher equalTo(ViewStats dto) {
        return new ViewStatsMatcher(dto);
    }

    @Override
    public boolean matches(ViewStats dto) {
        return HitFactory.equals(this.dto, dto);
    }
}