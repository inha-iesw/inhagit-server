package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.ValidSemesterNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 학기 이름이 유효한지 검사하는 Annotation
 */
@Constraint(validatedBy = ValidSemesterNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSemesterName {
    String message() default "올바른 학기 이름이 아닙니다. (예: 22-1학기)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}