package inha.git.common.validation.validator;

import inha.git.common.validation.annotation.UniqueTeamName;
import inha.git.team.domain.repository.TeamJpaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * UniqueTeamNameValidator는 팀 이름이 중복되는지 검증.
 */
@Component
@RequiredArgsConstructor
public class UniqueTeamNameValidator implements ConstraintValidator<UniqueTeamName, String> {

    private final TeamJpaRepository teamJpaRepository;

    /**
     * 팀 이름이 중복되는지 검증.
     *
     * @param teamName 팀 이름
     * @param context  ConstraintValidatorContext
     * @return 중복되지 않으면 true, 중복되면 false
     */
    @Override
    public boolean isValid(String teamName, ConstraintValidatorContext context) {
        if (teamName == null) {
            return true;
        }
        return !teamJpaRepository.existsByName(teamName);
    }
}