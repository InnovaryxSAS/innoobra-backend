package com.lambdas.validation.annotations;

import com.lambdas.validation.validators.ProjectStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProjectStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectStatusValid {
    String message() default "Status must be one of: active, inactive, pending, suspended, completed, cancelled";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}