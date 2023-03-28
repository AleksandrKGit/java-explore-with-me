package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.event.dto.EventUserStateAction;
import ru.practicum.ewm.main.validation.constraints.EventUserSate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EventUserSateValidator implements ConstraintValidator<EventUserSate, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            EventUserStateAction.valueOf(value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
