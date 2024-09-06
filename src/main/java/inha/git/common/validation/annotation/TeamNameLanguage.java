package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.TeamNameLanguageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TeamNameLanguage은 팀 이름이 한글 또는 영어로만 생성되는지 검증하는 어노테이션.
 */
@Constraint(validatedBy = TeamNameLanguageValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface TeamNameLanguage {

    String message() default "팀명은 한글 또는 영어로만 생성할 수 있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}