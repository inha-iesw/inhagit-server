package inha.git.common.validation.annotation;


import inha.git.common.validation.validator.UsernameUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 유저아이디 중복 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = UsernameUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameUnique {

    String message() default "이미 등록된 유저아이디입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
