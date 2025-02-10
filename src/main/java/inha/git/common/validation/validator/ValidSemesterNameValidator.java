package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidSemesterName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 학기 이름이 유효한지 검사하는 Validator
 */
public class ValidSemesterNameValidator implements ConstraintValidator<ValidSemesterName, String> {

    private static final String SEMESTER_NAME_REGEX = "^[0-9]{2}-[1-2]학기$";

    /**
     * 학기 이름이 유효한지 검사
     * @param value 검사할 학기 이름
     * @param context
     * @return 유효하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches(SEMESTER_NAME_REGEX);
    }
}
