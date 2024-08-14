package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 비밀번호 유효성 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "8~20글자, 하나 이상의 알파벳, 숫자, 특수 문자가 포함된 비밀번호를 입력하세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
