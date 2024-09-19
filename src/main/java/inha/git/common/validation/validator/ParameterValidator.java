package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidParameter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ParameterValidator
 * 파라미터 유효성 검사
 */
public class ParameterValidator implements ConstraintValidator<ValidParameter, String> {

    @Override
    public void initialize(ValidParameter constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return false; // null 또는 빈 문자열일 경우
        }
        return !email.contains(" ") && !email.contains("\u0000"); // 공백으로 시작하거나 null 문자가 포함된 경우
    }
}