package com.lambdas.validation.validators;

import com.lambdas.validation.annotations.UserStatusValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class UserStatusValidator implements ConstraintValidator<UserStatusValid, String> {
    private static final Set<String> VALID_STATUSES = Set.of(
        "active", "inactive", "pending", "suspended"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return VALID_STATUSES.contains(value.toLowerCase());
    }
}
