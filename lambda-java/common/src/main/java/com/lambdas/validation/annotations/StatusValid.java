package com.lambdas.validation.annotations;

import com.lambdas.validation.validators.StatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusValid {
    String message() default "Status must be one of: ACTIVE, INACTIVE, PENDING";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}