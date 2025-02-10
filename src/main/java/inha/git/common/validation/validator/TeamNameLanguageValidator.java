package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.TeamNameLanguage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * TeamNameLanguageValidator는 팀 이름이 한글 또는 영어로만 생성되는지 검증.
 */
public class TeamNameLanguageValidator implements ConstraintValidator<TeamNameLanguage, String> {

    private static final String KOREAN_ENGLISH_REGEX = "^[a-zA-Z가-힣\\s]+$";

    @Override
    public void initialize(TeamNameLanguage constraintAnnotation) {
    }

    /**
     * 팀 이름이 한글 또는 영어로만 생성되는지 검증.
     *
     * @param value   팀 이름
     * @param context ConstraintValidatorContext
     * @return 한글 또는 영어로만 생성되면 true, 그 외에는 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.matches(KOREAN_ENGLISH_REGEX);
    }
}
