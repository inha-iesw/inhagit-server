package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidUserNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 사용자 번호 유효성 검증을 위한 Validator.
 */
public class UserNumberValidator implements ConstraintValidator<ValidUserNumber, String> {

    // 학번, 교번 정규 표현식 = 6~8글자 숫자
    private static final String USER_NUMBER_REGEX = "^[0-9]{6,8}$";

    @Override
    public void initialize(ValidUserNumber constraintAnnotation) {}

    /**
     * 사용자 번호 유효성 검증.
     *
     * @param value 사용자 번호
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 사용자 번호가 유효하면 true, 유효하지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(USER_NUMBER_REGEX);
    }
}