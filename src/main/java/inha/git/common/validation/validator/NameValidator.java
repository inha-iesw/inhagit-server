package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 이름 유효성 검증을 위한 Validator.
 */
public class NameValidator implements ConstraintValidator<ValidName, String> {

    private static final String USER_NAME_REGEX = "^(?=.*[a-zA-Z가-힣])[a-zA-Z가-힣]{2,12}$";

    @Override
    public void initialize(ValidName constraintAnnotation) {}

    /**
     * 이름 유효성 검증.
     *
     * @param value 이름
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 이름이 유효하면 true, 유효하지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(USER_NAME_REGEX);
    }
}
