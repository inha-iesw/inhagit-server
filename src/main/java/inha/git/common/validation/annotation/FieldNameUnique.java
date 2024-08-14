package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.FieldNameUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


/**
 * 분야 이름 중복 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = FieldNameUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldNameUnique {

    String message() default "이미 등록된 분야입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
