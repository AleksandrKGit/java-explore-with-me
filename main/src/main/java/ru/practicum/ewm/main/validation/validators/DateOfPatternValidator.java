package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.validation.constraints.DateOfPattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateOfPatternValidator implements ConstraintValidator<DateOfPattern, String> {
    String pattern;

    @Override
    public void initialize(DateOfPattern constraintAnnotation) {
        pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}