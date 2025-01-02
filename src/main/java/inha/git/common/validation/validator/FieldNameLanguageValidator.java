package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.FieldNameLanguage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * FieldNameLanguageValidator는 FieldNameLanguage 어노테이션의 유효성 검사를 수행하는 클래스.
 */
public class FieldNameLanguageValidator implements ConstraintValidator<FieldNameLanguage, String> {

    private static final String KOREAN_ENGLISH_REGEX = "^[a-zA-Z가-힣\\s/]*$";

    @Override
    public void initialize(FieldNameLanguage constraintAnnotation) {
    }

    /**
     * 한글 또는 영어로만 구성되어 있는지 검사.
     *
     * @param value   검사할 값
     * @param context 컨텍스트
     * @return 유효성 검사 결과
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.matches(KOREAN_ENGLISH_REGEX);
    }
}
