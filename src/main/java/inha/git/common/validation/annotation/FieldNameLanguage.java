package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.FieldNameLanguageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * FieldNameLanguage 어노테이션은 한글 또는 영어로만 구성되어 있는지 검사하는 어노테이션
 */
@Constraint(validatedBy = FieldNameLanguageValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldNameLanguage {

    String message() default "분야는 한글 또는 영어로만 생성할 수 있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}