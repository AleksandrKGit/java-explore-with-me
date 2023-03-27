package ru.practicum.ewm.main.tools.matchers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalDateTimeMatcher extends BaseMatcher<LocalDateTime> {
    LocalDateTime date;
    DateTimeFormatter formatter;

    private LocalDateTimeMatcher(LocalDateTime date, DateTimeFormatter formatter) {
        this.date = date;
        this.formatter = formatter;
    }

    public static LocalDateTimeMatcher near(LocalDateTime date, String pattern) {
        return new LocalDateTimeMatcher(date, DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof String) {
            try {
                LocalDateTime dateItem = LocalDateTime.parse((String) item, formatter);
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