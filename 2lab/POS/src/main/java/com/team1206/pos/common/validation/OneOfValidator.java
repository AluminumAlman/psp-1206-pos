package com.team1206.pos.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

@Slf4j
public class OneOfValidator implements ConstraintValidator<OneOf, Object> {

    private String[] fields;

    @Override
    public void initialize(OneOf constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        log.debug("Validating object: {}", obj);
        if (fields == null || fields.length < 2) {
            throw new IllegalArgumentException(
                    "At least two fields must be specified for @OneOf validation.");
        }

        int nonNullCount = 0;

        try {
            for (String field : fields) {
                PropertyDescriptor pd = new PropertyDescriptor(field, obj.getClass());
                Method getter = pd.getReadMethod();
                Object value = getter.invoke(obj);

                if (value != null) {
                    nonNullCount++;
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error accessing fields for validation", e);
        }

        boolean isValid = (nonNullCount == 1);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(
                    "Only one of the following fields must be provided: %s",
                    String.join(", ", fields)
            )).addPropertyNode("general").addConstraintViolation();
        }

        return isValid;
    }
}