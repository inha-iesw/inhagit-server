package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.ParameterValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ValidParameter
 * 파라미터 유효성 검사 어노테이션
 */
@Constraint(validatedBy = ParameterValidator.class) // 검증기 클래스를 지정
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidParameter {
    String message() default "공백으로 있으면 안되고 널문자가 포함되면 안됩니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}