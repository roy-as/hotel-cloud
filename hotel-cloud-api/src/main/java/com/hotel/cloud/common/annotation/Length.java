package com.hotel.cloud.common.annotation;

import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.List;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = Length.ArrayLengthValidator.class)
public @interface Length {

    int size();

    String message() default "array length is ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    class ArrayLengthValidator implements ConstraintValidator<Length, List<?>> {

        private int minLength;

        @Override
        public void initialize(Length length) {
            this.minLength = length.size();
        }

        @Override
        public boolean isValid(List<?> value, ConstraintValidatorContext context) {
            return value.size() == this.minLength;
        }
    }

}
