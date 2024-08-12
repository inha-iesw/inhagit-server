package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.EmailUnique;
import inha.git.user.domain.repository.UserJpaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * 유저 email 중복 검증을 위한 Validator.
 */
@Component
@RequiredArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {

    private final UserJpaRepository userJpaRepository;

    /**
     * 유저 email 중복 검증을 위한 Validator 초기화.
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(EmailUnique constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
    /**
     * 유저 email 중복 검증.
     *
     * @param email 유저 email
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 유저아이디가 중복되지 않으면 true, 중복되면 false
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userJpaRepository.existsByEmail(email);
    }
}