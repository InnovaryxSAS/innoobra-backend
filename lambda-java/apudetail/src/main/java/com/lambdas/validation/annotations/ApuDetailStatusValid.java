package com.lambdas.validation.annotations;

import com.lambdas.validation.validators.ApuDetailStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ApuDetailStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApuDetailStatusValid {
    String message() default "Status must be one of: active, inactive";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}