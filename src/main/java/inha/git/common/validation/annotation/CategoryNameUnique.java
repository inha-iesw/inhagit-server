package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.CategoryNameUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 카테고리 이름 중복 검증을 위한 Annotation.
 */
@Documented
@Constraint(validatedBy = CategoryNameUniqueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryNameUnique {

    String message() default "이미 등록된 카테고리입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
