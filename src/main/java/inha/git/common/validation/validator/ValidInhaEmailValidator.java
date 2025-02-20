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

        String[] emailParts = email.split("@");
        if (emailParts.length != 2) {
            return false;
        }

        String localPart = emailParts[0];
        String domainPart = emailParts[1];

        if (!localPart.matches(EMAIL_PATTERN)) {
            return false;
        }
        return domainPart.equals("inha.edu") || domainPart.equals("inha.ac.kr");
    }
}
