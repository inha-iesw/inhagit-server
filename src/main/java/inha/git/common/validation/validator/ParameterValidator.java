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
            return false;
        }
        return !email.startsWith(" ") && !email.contains("\u0000");
    }
}
