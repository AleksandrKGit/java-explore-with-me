package ru.practicum.ewm.common.dto.tools.matchers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import java.time.LocalDateTime;

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
        if (item instanceof LocalDateTime) {
            try {
                LocalDateTime dateItem = (LocalDateTime) item;
                return !dateItem.isBefore(date.minusSeconds(2)) && !dateItem.isAfter(date.plusSeconds(2));
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
