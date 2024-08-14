package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.DepartmentNameUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


/**
 * 학과 이름 중복 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = DepartmentNameUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DepartmentNameUnique {

    String message() default "이미 등록된 학과입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
