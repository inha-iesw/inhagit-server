package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.CollegeNameFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 단과대 이름이 한글로 되어 있고 "대학"으로 끝나는지 검증하기 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = CollegeNameFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCollegeName {

    String message() default "단과대 이름은 한글로 작성되어야 하며 '대학'으로 끝나야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}