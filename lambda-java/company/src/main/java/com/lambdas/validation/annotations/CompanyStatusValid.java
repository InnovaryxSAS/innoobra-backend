package com.lambdas.validation.annotations;

import com.lambdas.validation.validators.CompanyStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CompanyStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompanyStatusValid {
    String message() default "Status must be one of: active, inactive, pending";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}