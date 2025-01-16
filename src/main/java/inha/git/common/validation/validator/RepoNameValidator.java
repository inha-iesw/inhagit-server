package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidRepoName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 저장소 이름 유효성 검증을 위한 Validator.
 */
public class RepoNameValidator implements ConstraintValidator<ValidRepoName, String> {

    private static final String REPO_NAME_REGEX = "^(?!-|\\.)[a-z0-9.-]{1,100}(?<!-)$";

    @Override
    public void initialize(ValidRepoName constraintAnnotation) {}

    /**
     * 저장소 이름 유효성 검증.
     *
     * @param value 저장소 이름
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 저장소 이름이 유효하면 true, 유효하지 않으면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(REPO_NAME_REGEX);
    }
}
