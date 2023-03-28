package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.event.dto.EventAdminStateAction;
import ru.practicum.ewm.main.validation.constraints.EventAdminSate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EventAdminSateValidator implements ConstraintValidator<EventAdminSate, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            EventAdminStateAction.valueOf(value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}