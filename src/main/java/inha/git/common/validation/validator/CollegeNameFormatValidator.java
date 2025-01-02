package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidCollegeName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 단과대 이름이 한글로 되어 있고 "대학"으로 끝나는지 검증하기 위한 Validator.
 */
public class CollegeNameFormatValidator implements ConstraintValidator<ValidCollegeName, String> {

    private static final String KOREAN_DEPARTMENT_NAME_REGEX = "^[가-힣]+대학$";

    @Override
    public void initialize(ValidCollegeName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * 단과대 이름이 한글로 되어 있고 "대학"으로 끝나는지 검증한다.
     *
     * @param name 단과대 이름
     * @param context 제약 조건에 대한 컨텍스트
     * @return 단과대 이름이 한글로 되어 있고 "대학"으로 끝나면 true, 그렇지 않으면 false
     */
    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isEmpty()) {
            return false; // null이나 빈 문자열은 유효하지 않다고 처리
        }
        return name.matches(KOREAN_DEPARTMENT_NAME_REGEX);
    }
}