package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidShare;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ShareValidator는 지분이 유효한지 검증하는 Validator 클래스입니다.
 */
public class ShareValidator implements ConstraintValidator<ValidShare, String> {
    private static final String SHARE_REGEX = "^\\d+\\.\\d{1,2}$"; // 소수점 둘째 자리까지 허용

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.matches(SHARE_REGEX);
    }
}
