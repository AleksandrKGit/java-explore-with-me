package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.validation.constraints.EventState;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EventStateValidator implements ConstraintValidator<EventState, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            ru.practicum.ewm.main.event.model.EventState.valueOf(value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}