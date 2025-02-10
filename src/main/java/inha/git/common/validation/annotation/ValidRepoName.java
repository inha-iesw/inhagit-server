package inha.git.common.validation.annotation;

import inha.git.common.validation.validator.RepoNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 저장소 이름 유효성 검증을 위한 애노테이션.
 */
@Documented
@Constraint(validatedBy = RepoNameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRepoName {

    String message() default "유효한 저장소 이름을 입력하세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
