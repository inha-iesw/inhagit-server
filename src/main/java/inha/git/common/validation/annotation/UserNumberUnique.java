package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.UserNumberUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserNumberUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserNumberUnique {

    String message() default "이미 등록된 학번/사번입니다.";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
