package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.SemesterNameUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 학기 이름 중복 검증을 위한 Annotation.
 */
@Documented
@Constraint(validatedBy = SemesterNameUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SemesterNameUnique {

    String message() default "이미 등록된 학기입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
