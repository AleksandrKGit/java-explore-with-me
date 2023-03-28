package ru.practicum.ewm.common.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFactory {
    public static final String DATE_FORMAT  = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_FORMATTER  = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static LocalDateTime now() {
        return dateOf(ofDate(LocalDateTime.now()));
    }

    public static LocalDateTime dateOf(String date) {
        return date == null ? null : LocalDateTime.parse(date, DATE_FORMATTER);
    }

    public static String ofDate(LocalDateTime date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }
}
