package com.lambdas.validation.annotations;

import com.lambdas.validation.validators.RoleStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleStatusValid {
    String message() default "Status must be one of: active, inactive, pending, suspended";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}