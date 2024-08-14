package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidDepartmentName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 학과 이름이 한글로 되어 있고 "학과"로 끝나는지 검증하기 위한 Validator.
 */
public class DepartmentNameFormatValidator implements ConstraintValidator<ValidDepartmentName, String> {

    private static final String KOREAN_DEPARTMENT_NAME_REGEX = "^[가-힣]+학과$";

    @Override
    public void initialize(ValidDepartmentName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isEmpty()) {
            return false; // null이나 빈 문자열은 유효하지 않다고 처리
        }
        return name.matches(KOREAN_DEPARTMENT_NAME_REGEX);
    }
}