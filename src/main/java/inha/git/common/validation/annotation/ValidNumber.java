package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.NumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 숫자 유효성 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = NumberValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNumber {

    String message() default "숫자만 입력하세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
