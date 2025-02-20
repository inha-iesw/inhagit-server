package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 이메일 주소 유효성 검증을 위한 Validator.
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9+\\-_.]+@[a-zA-Z0-9\\-]+\\.[a-zA-Z0-9\\-.]+$";

    @Override
    public void initialize(ValidEmail constraintAnnotation) {}

    /**
     * 이메일 주소 유효성 검증.
     *
     * @param value 이메일 주소
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 이메일 주소가 유효하면 true, 유효하지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(EMAIL_REGEX);
    }
}
