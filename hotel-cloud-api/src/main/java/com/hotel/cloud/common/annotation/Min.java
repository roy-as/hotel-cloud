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
@Constraint(validatedBy = Min.ListLengthValidator.class)
public @interface Min {

    int min() default 0;

    String message() default "element is not enough";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    class ListLengthValidator implements ConstraintValidator<Min, List<?>> {

        private int min;

        private int max;

        private boolean validateMax = false;

        private Min size;

        @Override
        public void initialize(Min length) {
            this.min = length.min();
        }

        @Override
        public boolean isValid(List<?> value, ConstraintValidatorContext context) {
            return value.size() >= this.min;
        }
    }

}
