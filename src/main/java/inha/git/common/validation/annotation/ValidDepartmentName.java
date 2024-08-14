package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.DepartmentNameFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 학과 이름이 한글로 되어 있고 "학과"로 끝나는지 검증하기 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = DepartmentNameFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDepartmentName {

    String message() default "학과 이름은 한글로 작성되어야 하며 '과'로 끝나야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}