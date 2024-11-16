package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.UniqueTeamNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UniqueTeamName은 팀 이름이 중복되는지 검증하는 어노테이션.
 */
@Constraint(validatedBy = UniqueTeamNameValidator.class) // Validator 클래스와 연결
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTeamName {

    String message() default "이미 존재하는 팀 이름입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}