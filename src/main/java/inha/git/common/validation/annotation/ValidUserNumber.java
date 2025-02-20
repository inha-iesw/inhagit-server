package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.UserNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 유저번호 유효성 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = UserNumberValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserNumber {

    String message() default "6~8자리의 숫자를 입력하세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
