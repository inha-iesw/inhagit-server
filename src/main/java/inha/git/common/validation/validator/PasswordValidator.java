package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 비밀번호 유효성 검증을 위한 Validator.
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String PASSWORD_REGEX = "^(?=.*?[0-9])(?=.*?[#?!@$%^&*\\-_]).{8,20}$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {}

    /**
     * 비밀번호 유효성 검증.
     *
     * @param value 비밀번호
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 비밀번호가 유효하면 true, 유효하지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(PASSWORD_REGEX);
    }
}
