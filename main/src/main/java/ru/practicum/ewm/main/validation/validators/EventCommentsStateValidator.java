package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.validation.constraints.EventCommentsState;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EventCommentsStateValidator implements ConstraintValidator<EventCommentsState, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            ru.practicum.ewm.main.event.model.EventCommentsState.valueOf(value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}

