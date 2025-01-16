package inha.git.problem.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemSubmit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ProblemSubmitJpaRepository는 Problem 제출 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemSubmitJpaRepository extends JpaRepository<ProblemSubmit, Integer> {
    Optional<ProblemSubmit> findByProblemRequestAndState(ProblemRequest problemRequest, State state);
}
