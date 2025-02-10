package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.ShareValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ShareValidator는 지분이 유효한지 검증하는 어노테이션입니다.
 */
@Documented
@Constraint(validatedBy = ShareValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidShare {
    String message() default "지분은 최대 소수점 두 자리까지 입력해야 합니다. (예: 16.24)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
