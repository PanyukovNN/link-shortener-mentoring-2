package ru.panyukovnn.linkshortener.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
public class ValidLocalDateTimeValidator implements ConstraintValidator<ValidLocalDateTime, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return false;
        }

        try {
            LocalDateTime.parse(value);

            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
