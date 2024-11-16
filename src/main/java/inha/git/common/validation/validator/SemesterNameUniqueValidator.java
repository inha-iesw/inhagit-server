package inha.git.common.validation.validator;

import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.validation.annotation.CollegeNameUnique;
import inha.git.common.validation.annotation.SemesterNameUnique;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static inha.git.common.BaseEntity.State.ACTIVE;

/**
 * 학기 이름 중복 검증을 위한 Validator.
 */
@Component
@RequiredArgsConstructor
public class SemesterNameUniqueValidator implements ConstraintValidator<SemesterNameUnique, String> {

    private final SemesterJpaRepository semesterJpaRepository;

    @Override
    public void initialize(SemesterNameUnique constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * 이름 중복 검증.
     *
     * @param name 이름
     * @param context 제약 위반 시 제약 위반 정보를 제공하는 객체
     * @return 이름이 중복되지 않으면 true, 중복되면 false
     */
    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return !semesterJpaRepository.existsByNameAndState(name, ACTIVE);
    }
}