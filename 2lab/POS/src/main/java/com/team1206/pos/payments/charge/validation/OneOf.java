package com.team1206.pos.payments.charge.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneOfValidator.class)
public @interface OneOf {
    String message() default "Only one of 'percent' or 'amount' must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}