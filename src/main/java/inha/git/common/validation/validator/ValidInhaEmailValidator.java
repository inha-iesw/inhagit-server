package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.ValidInhaEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidInhaEmailValidator implements ConstraintValidator<ValidInhaEmail, String> {

    private static final String EMAIL_PATTERN = "^(?!\\d+$)[a-zA-Z0-9._%+\\-!]+$";

    @Override
    public void initialize(ValidInhaEmail constraintAnnotation) {
    }


    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // 이메일을 '@' 기준으로 로컬 부분과 도메인 부분으로 나눔
        String[] emailParts = email.split("@");
        if (emailParts.length != 2) {
            return false;
        }

        String localPart = emailParts[0];
        String domainPart = emailParts[1];

        // 로컬 부분은 숫자만으로 이루어지지 않고, 특수문자를 포함하지 않아야 함
        if (!localPart.matches(EMAIL_PATTERN)) {
            return false;
        }
        // 도메인 부분은 'inha.edu' 또는 'inha.ac.kr'이어야 함
        return domainPart.equals("inha.edu") || domainPart.equals("inha.ac.kr");
    }
}