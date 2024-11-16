package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.NameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 이름 유효성 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = NameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName {

    String message() default "2~12글자 한글 또는 알파벳을 입력하세요 (모음 또는 자음만 구성된 한글은 안 됩니다).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
