package com.lambdas.validation.annotations;

import com.lambdas.validation.validators.UserStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserStatusValid {
    String message() default "Status must be one of: active, inactive, pending, suspended";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}