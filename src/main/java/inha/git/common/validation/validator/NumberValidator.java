package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 숫자 유효성 검증을 위한 Validator.
 */
public class NumberValidator implements ConstraintValidator<ValidNumber, String> {

    private static final String NUMBER_REGEX = "^[0-9]+$";

    @Override
    public void initialize(ValidNumber constraintAnnotation) {}

    /**
     * 숫자 유효성 검증.
     *
     * @param value 숫자
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 숫자가 유효하면 true, 유효하지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(NUMBER_REGEX);
    }
}
