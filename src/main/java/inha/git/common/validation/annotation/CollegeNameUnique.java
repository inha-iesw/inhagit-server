package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.CollegeNameUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CollegeNameUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CollegeNameUnique {

    String message() default "이미 등록된 단과대입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
