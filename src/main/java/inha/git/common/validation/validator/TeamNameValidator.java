package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidTeamName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 팀 이름 유효성 검증을 위한 Validator.
 */
public class TeamNameValidator implements ConstraintValidator<ValidTeamName, String> {

    // 팀 이름 정규 표현식 = 2~12글자 한글 또는 알파벳 (모음 또는 자음만 구성된 한글은 안 됨)
    private static final String TEAM_NAME_REGEX = "^(?=.*[a-zA-Z가-힣])[a-zA-Z가-힣]{2,12}$";

    @Override
    public void initialize(ValidTeamName constraintAnnotation) {}

    /**
     * 팀 이름 유효성 검증.
     *
     * @param value 팀 이름
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 팀 이름이 유효하면 true, 유효하지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(TEAM_NAME_REGEX);
    }
}