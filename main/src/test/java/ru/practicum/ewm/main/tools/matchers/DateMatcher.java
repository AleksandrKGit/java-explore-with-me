package ru.practicum.ewm.main.tools.matchers;

import java.time.LocalDateTime;
import static ru.practicum.ewm.common.support.DateFactory.*;

public class DateMatcher {
    public static boolean near(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null && date2 == null) {
            return true;
        }

        return date1 != null && date2 != null
                && !date1.isBefore(date2.minusSeconds(2))
                && !date1.isAfter(date2.plusSeconds(2));
    }

    public static boolean near(String date1, String date2) {
        return near(dateOf(date1), dateOf(date2));
    }
}