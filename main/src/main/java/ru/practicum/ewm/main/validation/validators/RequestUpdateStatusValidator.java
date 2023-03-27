package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.event.dto.RequestUpdateStatusAction;
import ru.practicum.ewm.main.validation.constraints.RequestUpdateStatus;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestUpdateStatusValidator implements ConstraintValidator<RequestUpdateStatus, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            RequestUpdateStatusAction.valueOf(value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}