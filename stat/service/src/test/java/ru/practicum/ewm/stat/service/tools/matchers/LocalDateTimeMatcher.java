package ru.practicum.ewm.stat.service.tools.matchers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import java.time.LocalDateTime;
import static ru.practicum.ewm.common.support.DateFactory.dateOf;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalDateTimeMatcher extends BaseMatcher<LocalDateTime> {
    LocalDateTime date;

    private LocalDateTimeMatcher(LocalDateTime date) {
        this.date = date;
    }

    public static LocalDateTimeMatcher near(LocalDateTime date) {
        return new LocalDateTimeMatcher(date);
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof String) {
            try {
                return DateMatcher.near(date, dateOf((String) item));
            } catch (Exception ignored) {
                return false;
            }
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("DateTime is near %s", date));
    }
}