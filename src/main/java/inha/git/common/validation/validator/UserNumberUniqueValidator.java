package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.UserNumberUnique;
import inha.git.user.domain.repository.UserJpaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static inha.git.common.BaseEntity.State.ACTIVE;

/**
 * 학번/사번 중복 검증을 위한 Validator.
 */

@Component
@RequiredArgsConstructor
public class UserNumberUniqueValidator implements ConstraintValidator<UserNumberUnique, String> {

    private final UserJpaRepository userJpaRepository;

    @Override
    public void initialize(UserNumberUnique constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }


    @Override
    public boolean isValid(String userNumber, ConstraintValidatorContext context) {
        return !userJpaRepository.existsByUserNumberAndState(userNumber, ACTIVE);
    }
}